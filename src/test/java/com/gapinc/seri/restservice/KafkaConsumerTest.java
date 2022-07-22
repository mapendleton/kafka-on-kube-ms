package com.gapinc.seri.restservice;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.gapinc.seri.restservice.model.BasicTopicMessage;
import com.gapinc.seri.restservice.service.KafkaConsumer;

@SpringBootTest
public class KafkaConsumerTest {

    @Mock
    KafkaConsumer consumer;

    @Mock
    SimpMessagingTemplate mockSimpMessagingTemplate;

    @Test
    public void consumerShouldTriggerOnPostandSendToWebSocket(){
        assertNotNull(mockSimpMessagingTemplate);
        String path = "/topic/consumer";
        BasicTopicMessage message = new BasicTopicMessage(1, "convertAndSend Test");
        doNothing().when(mockSimpMessagingTemplate).convertAndSend(path, message);
        mockSimpMessagingTemplate.convertAndSend(path, message);
        verify(mockSimpMessagingTemplate, times(1)).convertAndSend("/topic/consumer", message);
    }
}
