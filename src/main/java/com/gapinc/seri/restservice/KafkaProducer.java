package com.gapinc.seri.restservice;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private final KafkaTemplate<Integer,String> producer;
    
    @Autowired
    public KafkaProducer(final KafkaTemplate<Integer,String> producer){
        this.producer = producer;
    }

    public void send(String topic, BasicTopicMessage message){
        final ProducerRecord<Integer,String> record = new ProducerRecord<Integer, String> (topic, message.getId(), message.getContent());
        producer.send(record);
    }
}
