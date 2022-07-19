package com.gapinc.seri.restservice;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
public class WebsocketTest {

    @LocalServerPort
    private Integer port;

    private WebSocketStompClient stompClient;
    
    @BeforeAll
    void setup() {
        this.stompClient = new WebSocketStompClient(new SockJsClient(
            List.of(new WebSocketTransport(new StandardWebSocketClient()))
        ));
    }

    @Test
    void verifyMessageIsReceived() throws Exception {
    
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);
    
        stompClient.setMessageConverter(new StringMessageConverter());

        StompSession session = stompClient
            .connect(String.format("ws://localhost:%d/kafka-consumer", port), new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS); //Future.get() to block until connection ready
    
        session.subscribe("/topic/consumer", new StompFrameHandler() {
    
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }
        
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((String) payload);
            }
        });
        
        session.send("/topic/consumer", "Testing Time");
    
        await()
            .atMost(1, TimeUnit.SECONDS)
            .untilAsserted(() -> assertEquals("Testing Time", blockingQueue.poll()));
    }

}
