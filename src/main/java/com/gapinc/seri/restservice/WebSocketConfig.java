package com.gapinc.seri.restservice;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        // ui client will use this to connect to the server
        registry.addEndpoint("/ws-chat").setAllowedOrigins("http://localhost:3000").withSockJS();
        registry.addEndpoint("/ws").setAllowedOrigins("chrome-extension://fgponpodhbmadfljofbimhhlengambbn/index.html").withSockJS();
        registry.addEndpoint("/test").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic/");
    }
}
