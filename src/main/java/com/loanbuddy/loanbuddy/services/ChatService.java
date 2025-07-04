package com.loanbuddy.loanbuddy.services;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.loanbuddy.loanbuddy.Security.JwtUtil;
import com.loanbuddy.loanbuddy.model.ChatMessage;
import com.loanbuddy.loanbuddy.repository.ChatMessageRepository;

import io.jsonwebtoken.ExpiredJwtException;

@Service
public class ChatService {
    private final ChatMessageRepository repository;
    
    @Autowired
    private JwtUtil jwtUtil;

    public ChatService(ChatMessageRepository repository) {
        this.repository = repository;
    }

    public List<ChatMessage> getAllMessages(String token) {
        String username = extractUsernameSafely(token);
        if (!jwtUtil.validateToken(token, username)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        return repository.findAll();
    }

    public ChatMessage saveMessage(ChatMessage message, String token) {
        String username = extractUsernameSafely(token);
        if (!jwtUtil.validateToken(token, username)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        return repository.save(message);
    }

    public List<ChatMessage> getMessagesBySenderAndReceiver(String sender, String receiver, String token) {
        String username = extractUsernameSafely(token);
        if (!jwtUtil.validateToken(token, username)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        return repository.findBySenderAndReceiver(sender, receiver);
    }

    private String extractUsernameSafely(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        try {
            return jwtUtil.extractUsername(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token format"));
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("Token has expired, please login again");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token provided");
        }
    }

    public void notifyReceiver(ChatMessage savedMessage) {
        try {
            // Create a WebSocket client
            WebSocketClient client = new StandardWebSocketClient();
            WebSocketStompClient stompClient = new WebSocketStompClient(client);
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());

            // Connect to WebSocket endpoint
            String wsUrl = "ws://localhost:8080/chat";
            StompSessionHandler sessionHandler = new DefaultStompSessionHandler();
            StompSession session = stompClient.connect(wsUrl, sessionHandler).get();

            // Send message to specific user's topic
            String destination = "/topic/messages/" + savedMessage.getReceiver();
            session.send(destination, savedMessage);

            // Disconnect after sending
            session.disconnect();
        } catch (Exception e) {
            throw new RuntimeException("Failed to notify receiver via WebSocket: " + e.getMessage());
        }
    }
}

class DefaultStompSessionHandler implements StompSessionHandler {
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        // No implementation needed for this use case
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        // No implementation needed for this use case
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        // No implementation needed for this use case
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        // No implementation needed for this use case
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return byte[].class;
    }
}
