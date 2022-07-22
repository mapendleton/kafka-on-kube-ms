package com.gapinc.seri.restservice.service;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Service;

@Service
public class SessionHandler extends StompSessionHandlerAdapter{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String subscriptionTopic;
    public BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();

    @Autowired
    public SessionHandler(@Value("${web-socket.destination}")String subscriptionTopic){
        this.subscriptionTopic = subscriptionTopic;
    }

    @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            session.subscribe(subscriptionTopic, this);
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            logger.error("Headers: {}\n",headers.toString());
            logger.error("Stomp Error:", exception);
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            super.handleTransportError(session, exception);
            logger.error("Stomp Transport Error:", exception);
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return String.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object payload) {
            blockingQueue.add((String) payload);
        }

}
