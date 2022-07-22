package com.gapinc.seri.restservice;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gapinc.seri.restservice.controllers.KafkaController;
import com.gapinc.seri.restservice.service.KafkaProducer;
import com.gapinc.seri.restservice.service.SessionHandler;

import io.netty.util.concurrent.Future;

import com.gapinc.seri.restservice.model.BasicTopicMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.awaitility.Awaitility.await;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.record.RecordMetaData;
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
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(
    properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@EmbeddedKafka
//@ContextConfiguration(classes = {String.class, ObjectMapper.class,KafkaListenerEndpointRegistry.class})
public class KafkaControllerIntegrationTest {

    final MockMvc mvc;
    final ObjectMapper objectMapper;
    final String topic;
    final KafkaListenerEndpointRegistry registry;
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    final BasicTopicMessage message = new BasicTopicMessage(1, "mvcTest");
    final SessionHandler sessionHandler;
    final private WebSocketStompClient stompClient;
    
    @Mock
    KafkaProducer kafkaProducer;

    @InjectMocks
    KafkaController kafkaController;

    @Autowired
    public KafkaControllerIntegrationTest(MockMvc mvc,ObjectMapper objectMapper,KafkaListenerEndpointRegistry registry, @Value("${spring.kafka.topic}") String topic, SessionHandler sessionHandler){
        this.mvc = mvc;
        this.objectMapper = objectMapper;
        this.topic = topic;
        this.registry = registry;
        this.sessionHandler = sessionHandler;
        stompClient = new WebSocketStompClient(new SockJsClient(
            List.of(new WebSocketTransport(new StandardWebSocketClient()))
        ));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void postListenandSendtoWebSocket() throws Exception {
        

        BlockingQueue<ConsumerRecord<Integer, String>> records = new LinkedBlockingQueue<>();
        //Gets @KafkaListener by id
        ConcurrentMessageListenerContainer<Integer, String> container = (ConcurrentMessageListenerContainer<Integer, String>) registry.getListenerContainer("kafkaListener");
        container.stop();
        AcknowledgingConsumerAwareMessageListener<Integer,String> messageListener = (AcknowledgingConsumerAwareMessageListener<Integer, String>) container.getContainerProperties().getMessageListener();
        container.getContainerProperties().setMessageListener(new AcknowledgingConsumerAwareMessageListener<Integer,String>() {
            //container must be stopped to modify, then when message is received we override to pull message into blocking queue
            @Override
            public void onMessage(ConsumerRecord<Integer,String> data, Acknowledgment ack, Consumer<?,?> consumer) {
                messageListener.onMessage(data,ack,consumer);
                records.add(data);
            }
        });            
        container.start();

        stompClient.connect("ws://localhost:8084/kafka-consumer", sessionHandler)
        .get(5, TimeUnit.SECONDS);

        objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        //send message to embedded kafka broker topic
        MvcResult mvcRes = mvc.perform(MockMvcRequestBuilders.post("/topics/{topic}", topic)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaTypes.HAL_JSON)
            //.content("{\"id\": 1, \"content\": \"mvcTest\"}"))
            .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.producerRecord.value").value("mvcTest"))
                .andReturn();

        // await()
        // .atMost(5, TimeUnit.SECONDS)
        // .untilAsserted(() -> assertEquals("{\"id\": 1, \"content\": \"mvcTest\"}",sessionHandler.blockingQueue.poll()));

        //poll the blocking queue which indicates the listener was called
        ConsumerRecord<Integer,String> consumedMessage = records.poll(500, TimeUnit.MILLISECONDS);
        assertNotNull(consumedMessage);
        assertEquals(1, consumedMessage.key());
        assertEquals("mvcTest", consumedMessage.value());
        container.stop();
    }
}
