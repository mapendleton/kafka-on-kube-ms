package com.gapinc.seri.restservice.service;

import org.springframework.stereotype.Service;

import com.gapinc.seri.restservice.model.BasicTopicMessage;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
public class KafkaConsumer {

    @Autowired
    SimpMessagingTemplate template;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @KafkaListener(topics = "${spring.kafka.topic}", id = "kafkaListener")
    public void consume(ConsumerRecord<Integer,String> message) throws MessagingException{
        try {
            template.convertAndSend("/topic/consumer", new BasicTopicMessage(message.key(), message.value()));
            logger.info("Message consumed on topic: {},Key: {}, Message: {}", message.topic(),message.key(), message.value());
        } catch (MessagingException me) {
            String err = String.format("Error converting and sending to {} Key: {} Value : {} Error : {}",message.topic(), message.key(),message.value(), me.getMessage());
            logger.error(err);
            throw new MessagingException(err);
        }
        
    }
}
