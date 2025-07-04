package com.loanbuddy.loanbuddy.config;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanbuddy.loanbuddy.model.ChatMessage;

@Component
public class ChatHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final Set<String> onlineUsers = new CopyOnWriteArraySet<>();  // ✅ Track online users
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = getUserId(session);
        if (userId != null && !userId.isEmpty()) {
            userSessions.put(userId, session);
            onlineUsers.add(userId); // ✅ Add user to online list
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
        chatMessage.setTimestamp(LocalDateTime.now());

        String receiver = chatMessage.getReceiver();
        WebSocketSession receiverSession = userSessions.get(receiver);

        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
        } else {
            // Notify sender that the recipient is offline
            ChatMessage offlineMessage = new ChatMessage();
            offlineMessage.setSender("System");
            offlineMessage.setReceiver(chatMessage.getSender());
            offlineMessage.setContent("User " + receiver + " is offline.");
            offlineMessage.setTimestamp(LocalDateTime.now());

            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(offlineMessage)));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = getUserId(session);
        if (userId != null) {
            userSessions.remove(userId);
            onlineUsers.remove(userId);  // ✅ Remove user from online list
        }
    }

    private String getUserId(WebSocketSession session) {
        return UriComponentsBuilder.fromUri(session.getUri())
                .build()
                .getQueryParams()
                .getFirst("userId");
    }

    public Set<String> getOnlineUsers() {
        return onlineUsers; // ✅ Expose online users
    }
}
