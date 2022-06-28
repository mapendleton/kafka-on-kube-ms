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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.AcknowledgingConsumerAwareMessageListener;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@AutoConfigureMockMvc
@EmbeddedKafka
public class KafkaControllerTest {

    final MockMvc mvc;
    final ObjectMapper objectMapper;
    final EmbeddedKafkaBroker embeddedKafkaBroker;
    final String topic;
    final KafkaListenerEndpointRegistry registry;
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    final BasicTopicMessage message = new BasicTopicMessage(1, "This is a test.");

    @InjectMocks
    KafkaController kafkaController;

    @Mock
    KafkaProducer kafkaProducer;

    @Autowired
    public KafkaControllerTest(MockMvc mvc,ObjectMapper objectMapper,EmbeddedKafkaBroker embeddedKafkaBroker, KafkaListenerEndpointRegistry registry, @Value("${spring.kafka.topic}") String topic){
        this.mvc = mvc;
        this.objectMapper = objectMapper;
        this.embeddedKafkaBroker = embeddedKafkaBroker;
        this.topic = topic;
        this.registry = registry;
    }

    @Test
    public void testProduce_Consume() throws Exception {

        ConcurrentMessageListenerContainer<?, ?> container = (ConcurrentMessageListenerContainer<?, ?>) registry.getListenerContainer("kafkaListener");
        container.stop();
        @SuppressWarnings("unchecked")
        AcknowledgingConsumerAwareMessageListener<Integer,String> messageListener = (AcknowledgingConsumerAwareMessageListener<Integer, String>) container.getContainerProperties().getMessageListener();
        BlockingQueue<ConsumerRecord<Integer, String>> records = new LinkedBlockingQueue<>();
        container.getContainerProperties().setMessageListener(new AcknowledgingConsumerAwareMessageListener<Integer,String>() {
            @Override
            public void onMessage(ConsumerRecord<Integer,String> data, Acknowledgment ack, Consumer<?,?> consumer) {
                messageListener.onMessage(data,ack,consumer);
                records.add(data);
            }
        });            
        container.start();

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
        container.stop();
    }

    @Test
    public void testProduceFailure() throws JsonProcessingException, Exception {
        when(kafkaProducer.send(topic, message)).thenThrow(new InterruptedException("Test Exception"));
        ResponseEntity<?> response = kafkaController.sendMessageToTopic(message, topic);

        logger.info("actual: " + response.getStatusCode().name());
        logger.info("\nexpected: " + HttpStatus.INTERNAL_SERVER_ERROR);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
