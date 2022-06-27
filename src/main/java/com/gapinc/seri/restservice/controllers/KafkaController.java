package com.gapinc.seri.restservice.controllers;

import java.util.concurrent.ExecutionException;
import com.gapinc.seri.restservice.model.BasicTopicMessage;
import com.gapinc.seri.restservice.service.KafkaProducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaController {
    
    final KafkaProducer producer;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    public KafkaController(final KafkaProducer producer){
        this.producer = producer;
    }

    @PostMapping("topics/{topic}")
    public ResponseEntity<?> sendMessageToTopic(@RequestBody BasicTopicMessage message, @PathVariable String topic) throws InterruptedException, ExecutionException{
        try {
            producer.send(topic, message);
        } catch (InterruptedException | ExecutionException e) {
            String err = String.format("Failed to send message to %s%n", topic);
            logger.error(err + e.getStackTrace());
            return new ResponseEntity<>(
                err,
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        return new ResponseEntity<>(
            message,
            HttpStatus.CREATED
        );
    }
}
