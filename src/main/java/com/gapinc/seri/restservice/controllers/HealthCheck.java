package com.gapinc.seri.restservice.controllers;

import com.gapinc.seri.restservice.model.BasicTopicMessage;
import com.gapinc.seri.restservice.service.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
public class HealthCheck {
    @Autowired
    public HealthCheck(){

    }

    @GetMapping("health")
    public ResponseEntity<?> health() {
        return new ResponseEntity<>(
                "OK",
                HttpStatus.OK
        );
    }
}
