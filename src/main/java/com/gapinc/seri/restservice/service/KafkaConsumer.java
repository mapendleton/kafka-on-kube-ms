package com.gapinc.seri.restservice.service;

import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;

@Service
public class KafkaConsumer {
    
    @KafkaListener(topics = "my-topic", groupId = "group-id")
    public void consume(String message){
        System.out.print("Message : "+message);
    }
}
