// package com.loanbuddy.loanbuddy.config;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.socket.config.annotation.EnableWebSocket;
// import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
// import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
// import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

// @Configuration
// @EnableWebSocket
// public class WebSocketConfig implements WebSocketConfigurer {

//     @Override
//     public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//         registry.addHandler(new ChatHandler(), "/ws/chat")
//                 .setAllowedOrigins("http://localhost:3000",
//                 "http://localhost:3001",
//                 "http://localhost:3002")
//                 .addInterceptors(new HttpSessionHandshakeInterceptor());
//                 // REMOVE .withSockJS() for pure WebSockets
//     }
// }
package com.loanbuddy.loanbuddy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/topic"); // Allows subscriptions
        registry.setApplicationDestinationPrefixes("/app"); // Routes messages to controllers
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // WebSocket Endpoint
                .setAllowedOrigins("http://localhost:3000",
                                   "http://localhost:3001",
                                   "http://localhost:3002")
                .withSockJS(); // Enable SockJS fallback for browsers that don’t support WebSockets
    }
}
