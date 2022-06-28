package com.gapinc.seri.restservice.service;

import org.springframework.stereotype.Service;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

@Service
public class KafkaConsumer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @KafkaListener(topics = "${spring.kafka.topic}", id = "kafkaListener")
    public void consume(ConsumerRecord<Integer,String> message){
        logger.info("Message consumed on topic: {},Key: {}, Message: {}", message.topic(),message.key(), message.value());
    }
}
