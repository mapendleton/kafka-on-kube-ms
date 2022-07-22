package com.gapinc.seri.restservice.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.gapinc.seri.restservice.model.BasicTopicMessage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private final KafkaTemplate<Integer,String> producer;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    public KafkaProducer(final KafkaTemplate<Integer,String> producer){
        this.producer = producer;
    }

    public SendResult<Integer,String> send(String topic, BasicTopicMessage message) throws InterruptedException, ExecutionException {
        final ProducerRecord<Integer,String> record = new ProducerRecord<Integer, String> (topic, message.getId(), message.getContent());
        try {
            Future<SendResult<Integer,String>> result = producer.send(record);
            logger.info("Sending Message: {} to: {}",message.getContent(),topic);
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            //should log
            logger.error("Attempted to send message : key=%s, message=%s%n", message.getId(), message.getContent());
            logger.error(e.getMessage());
            throw e;
        }
    }
}
