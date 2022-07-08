package com.gapinc.seri.restservice.service;

import org.springframework.stereotype.Service;

import com.gapinc.seri.restservice.model.BasicTopicMessage;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
public class KafkaConsumer {

    @Autowired
    SimpMessagingTemplate template;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @KafkaListener(topics = "${spring.kafka.topic}", id = "kafkaListener")
    //@SendTo("/topic/consumer")
    public void consume(ConsumerRecord<Integer,String> message){
        try {
            BasicTopicMessage btp = new BasicTopicMessage(message.key(), message.value());
            template.convertAndSend("/topic/consumer", message.value());
            //logger.info("Message consumed on topic: {},Key: {}, Message: {}", message.topic(),message.key(), message.value());
            logger.info("Message consumed, Key: {}, Message: {}",btp.getId(),btp.getContent());
            //return new BasicTopicMessage(message.key(), message.value());
        } catch (Exception e) {
            logger.error("my god IDK what is happening: {}", e.getMessage());
            throw e;
        }

    }
}
