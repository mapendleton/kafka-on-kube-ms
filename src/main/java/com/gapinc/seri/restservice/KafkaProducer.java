package com.gapinc.seri.restservice;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
public class KafkaProducer {
    private final KafkaTemplate<Integer,String> producer;
    
    @Autowired
    public KafkaProducer(final KafkaTemplate<Integer,String> producer){
        this.producer = producer;
    }

    public SendResult<Integer,String> send(String topic, BasicTopicMessage message) throws InterruptedException, ExecutionException {
        final ProducerRecord<Integer,String> record = new ProducerRecord<Integer, String> (topic, message.getId(), message.getContent());
        try {
            ListenableFuture<SendResult<Integer,String>> result = producer.send(record);
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            //should log
            System.out.printf("Attempted to send message : key=%s, message=%s%n", message.getId(), message.getContent());
            System.out.print(e.getMessage());
            throw e;
        }
    }
}
