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
    
    final KafkaProducer producer;
    
    @Autowired
    public KafkaController(final KafkaProducer producer){
        this.producer = producer;
    }

    @PostMapping("topics/{topic}")
    public ResponseEntity<?> sendMessageToTopic(@RequestBody BasicTopicMessage message, @PathVariable String topic) {
        producer.send(topic, message);

        return new ResponseEntity<>(
            message,
            HttpStatus.CREATED
        );
    }
}