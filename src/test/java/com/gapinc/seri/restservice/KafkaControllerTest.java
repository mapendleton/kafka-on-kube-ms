package com.gapinc.seri.restservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gapinc.seri.restservice.controllers.KafkaController;
import com.gapinc.seri.restservice.service.KafkaProducer;
import com.gapinc.seri.restservice.model.BasicTopicMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@AutoConfigureMockMvc
@EmbeddedKafka
public class KafkaControllerTest {

    final MockMvc mvc;
    final ObjectMapper objectMapper;
    final EmbeddedKafkaBroker embeddedKafkaBroker = new EmbeddedKafkaBroker();
    final String topic;

    private KafkaMessageListenerContainer<Integer, String> container;
    private BlockingQueue<ConsumerRecord<Integer, String>> records;

    @InjectMocks
    KafkaController kafkaController;

    @Mock
    KafkaProducer kafkaProducer;

    @Autowired
    public KafkaControllerTest(MockMvc mvc,ObjectMapper objectMapper,EmbeddedKafkaBroker embeddedKafkaBroker, @Value("${test.topic}") String topic){
        this.mvc = mvc;
        this.objectMapper = objectMapper;
        this.embeddedKafkaBroker = embeddedKafkaBroker;
        this.topic = topic;
    }

    @Test
    public void testProduceFailure() throws JsonProcessingException, Exception {
        BasicTopicMessage message = new BasicTopicMessage(1, "This is a test.");
        when(kafkaProducer.send(topic, message)).thenThrow(new InterruptedException("Test Exception"));
        ResponseEntity<?> response = kafkaController.sendMessageToTopic(message, topic);

        System.out.print("actual: " + response.getStatusCode().name());
        System.out.print("\nexpected: " + HttpStatus.INTERNAL_SERVER_ERROR);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class KafkaControllerTestNested{
        @BeforeAll
        void setUp() {
    
            DefaultKafkaConsumerFactory<Integer, String> consumerFactory = new DefaultKafkaConsumerFactory<>(getConsumerProperties());
            ContainerProperties containerProperties = new ContainerProperties(topic);
            container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
            records = new LinkedBlockingQueue<>();
            container.setupMessageListener((MessageListener<Integer,String>) records::add);
            container.start();
            ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
        }
    
        private Map<String,Object> getConsumerProperties(){
            return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                ConsumerConfig.GROUP_ID_CONFIG, "consumer",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true",
                ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "10",
                ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "60000",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        }
    
        @AfterAll
        void tearDown() {
            container.stop();
        }

        /**
         * This method uses the embedded kafka env to test the KafkaController.postToTopic()
         * @throws Exception
         */
        @Test
        public void testProduce() throws Exception {

            BasicTopicMessage message = new BasicTopicMessage(1, "This is a test.");
            mvc.perform(MockMvcRequestBuilders.post("/topics/{topic}", topic)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(message)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.content").value("This is a test."));

            ConsumerRecord<Integer,String> consumedMessage = records.poll(500, TimeUnit.MILLISECONDS);
            assertNotNull(consumedMessage);
            assertEquals(1, consumedMessage.key());
            assertEquals("This is a test.", consumedMessage.value());
        }
    }
}
