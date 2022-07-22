package com.gapinc.seri.restservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.CompletableToListenableFutureAdapter;
import com.gapinc.seri.restservice.service.KafkaProducer;

@SpringBootTest
public class KafkaProducerTest {

    @Mock(name = "producer")
    private KafkaTemplate<Integer,String> mockKafkaTemplate;

    @InjectMocks
    private KafkaProducer producer; 

    String topic = "topic";
    final SendResult<Integer,String> sendResult; 
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public KafkaProducerTest(){
        sendResult = new SendResult<>(
            new ProducerRecord<Integer,String>(topic, KafkaControllerTest.message.getId(), KafkaControllerTest.message.getContent()),
            new RecordMetadata(new TopicPartition(topic, 0), 0, 0, 0, 0, 0)
            );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void sendShouldReturnSendResult() throws InterruptedException, ExecutionException{
        when(mockKafkaTemplate.send(sendResult.getProducerRecord()))
            .thenReturn(new CompletableToListenableFutureAdapter(CompletableFuture.completedFuture(sendResult)));

        SendResult<Integer,String> result = producer.send("topic", KafkaControllerTest.message);
        assertEquals(sendResult, result);
    }
}
