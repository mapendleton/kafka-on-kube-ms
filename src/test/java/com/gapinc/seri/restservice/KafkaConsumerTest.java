package com.gapinc.seri.restservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.converter.MessageConverter;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.gapinc.seri.restservice.model.BasicTopicMessage;
import com.gapinc.seri.restservice.service.KafkaConsumer;

@SpringBootTest
public class KafkaConsumerTest {

    @InjectMocks
    KafkaConsumer consumer;

    @Mock
    SimpMessagingTemplate mockSimpMessagingTemplate;

    ConsumerRecord<Integer,String> consumerRecord = new ConsumerRecord<Integer,String>("testTopic",0,0,1,"convertAndSend Test");
    private BasicTopicMessage message = new BasicTopicMessage(consumerRecord.key(), consumerRecord.value());
    private String path = "/topic/consumer";

    @Test
    public void consumerShouldTriggerOnPostandSendToWebSocket(){

        doNothing().when(mockSimpMessagingTemplate).convertAndSend(path, message);
        consumer.consume(consumerRecord);
        verify(mockSimpMessagingTemplate, times(1)).convertAndSend("/topic/consumer", message);
    }

    @Test
    public void shouldCatchError(){
        doThrow(new MessagingException("BadMessage")).when(mockSimpMessagingTemplate).convertAndSend(path, message);
        MessagingException thrown = assertThrows(MessagingException.class, () -> {
            consumer.consume(consumerRecord);
        });

        assertEquals("Error converting and sending to {} Key: {} Value : {} Error : {}", thrown.getMessage());
    }
}
