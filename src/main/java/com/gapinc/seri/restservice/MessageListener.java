package com.gapinc.seri.restservice;

import com.gapinc.seri.restservice.model.BasicTopicMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {
    @Autowired
    SimpMessagingTemplate template;

    @KafkaListener(
            topics = "uitest",
            groupId = "uitestinggroup"
    )
    public void listen(@Payload String data, @Headers MessageHeaders headers){
        System.out.println("sending via kafka listener.." + data);
      template.convertAndSend("/topic/group", data);
    }
}
