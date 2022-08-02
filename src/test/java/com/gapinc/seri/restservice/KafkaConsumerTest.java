package com.gapinc.seri.restservice;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.gapinc.seri.restservice.model.BasicTopicMessage;
import com.gapinc.seri.restservice.service.KafkaConsumer;

@SpringBootTest
public class KafkaConsumerTest {

    @InjectMocks
    KafkaConsumer consumer;

    @Mock
    SimpMessagingTemplate mockSimpMessagingTemplate;

    @Test
    public void consumerShouldTriggerOnPostandSendToWebSocket(){
        ConsumerRecord<Integer,String> consumerRecord = new ConsumerRecord<Integer,String>("testTopic",0,0,1,"convertAndSend Test");
        BasicTopicMessage message = new BasicTopicMessage(consumerRecord.key(), consumerRecord.value());
        String path = "/topic/consumer";
        doNothing().when(mockSimpMessagingTemplate).convertAndSend(path, message);
        consumer.consume(consumerRecord);
        verify(mockSimpMessagingTemplate, times(1)).convertAndSend("/topic/consumer", message);
    }
}
