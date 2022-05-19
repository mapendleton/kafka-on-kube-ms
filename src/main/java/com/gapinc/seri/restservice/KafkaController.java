package com.gapinc.seri.restservice;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaController {
    
    //private final Producer<Integer,String> producer;
    private final KafkaTemplate<Integer,String> producer;
    
    @Autowired
    public KafkaController(final KafkaTemplate<Integer,String> producer){
        this.producer = producer;
    }

    @PostMapping("topics/{topic}")
    public ResponseEntity<?> sendMessageToTopic(@RequestBody BasicTopicMessage message, @PathVariable String topic) throws InterruptedException, ExecutionException{
        final ProducerRecord<Integer,String> pRecord = new ProducerRecord<>(topic, message.getId(), message.getContent());
        producer.send(pRecord);
        //producer.send(topic, message.getId(), message.getContent());

        return new ResponseEntity<>(
            message,
            HttpStatus.OK
        );
    }
}
