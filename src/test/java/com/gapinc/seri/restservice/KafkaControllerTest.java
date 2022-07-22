package com.gapinc.seri.restservice;

import static org.mockito.Mockito.when;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gapinc.seri.restservice.model.BasicTopicMessage;
import com.gapinc.seri.restservice.service.KafkaProducer;

@WebMvcTest
public class KafkaControllerTest {
    
    private final MockMvc mvc;
    private final ObjectMapper objectMapper;
    private final String topic = "test-topic";
    private final String body = "{\"id\": 1, \"content\": \"this will not be the return message\"}";
    static final BasicTopicMessage message = new BasicTopicMessage(1, "This is a KafkaController test.");
    private final SendResult<Integer,String> sendResult; 
    
    @MockBean
    private KafkaProducer mockProducer;

    @Autowired
    public KafkaControllerTest(MockMvc mvc, ObjectMapper objectMapper){
        this.mvc = mvc;
        this.objectMapper = objectMapper;
        sendResult = new SendResult<>(
            new ProducerRecord<Integer,String>(topic, message.getId(), message.getContent()),
            new RecordMetadata(new TopicPartition(topic, 0), 0, 0, 0, 0, 0)
            );
    }

    @Test
    public void postShouldReturnOk() throws JsonProcessingException, Exception{

            
        objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        when(mockProducer.send(topic, message)).thenReturn(sendResult);

        mvc.perform(MockMvcRequestBuilders.post("/topics/{topic}", topic)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaTypes.HAL_JSON)
        .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.producerRecord.value").value("This is a KafkaController test."));
    }

    @Test
    public void interruptedExceptionShouldSentServerError() throws JsonProcessingException, Exception {
        when(mockProducer.send(topic, message)).thenThrow(new InterruptedException("Test Exception"));
        mvc.perform(MockMvcRequestBuilders.post("/topics/{topic}", topic)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaTypes.HAL_JSON)
        .content(body))
            .andExpect(status().isInternalServerError());
    }

    @Test
    public void genericExceptionShouldReturnServiceUnavailable() throws JsonProcessingException, Exception {
        when(mockProducer.send(topic, message)).thenThrow(new RuntimeException());
        mvc.perform(MockMvcRequestBuilders.post("/topics/{topic}", topic)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaTypes.HAL_JSON)
        .content(body))
            .andExpect(status().isServiceUnavailable());
    }
}
