package com.loanbuddy.loanbuddy.controller;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loanbuddy.loanbuddy.Exceptions.ResourceNotFoundException;
import com.loanbuddy.loanbuddy.config.ChatHandler;
import com.loanbuddy.loanbuddy.model.ChatMessage;
import com.loanbuddy.loanbuddy.services.ChatService;
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    ChatHandler chatHandler;
    
    private final ChatService chatService;
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    @GetMapping("/online-users")
    public Set<String> getOnlineUsers() {
        return chatHandler.getOnlineUsers();  // ✅ Return online users
    }
    // to get all message.
    @GetMapping("/messages")
    public List<ChatMessage> getAllMessages(@RequestHeader("Authorization") String token) {
        logger.info("Received request to get all messages");
        try {
            String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            List<ChatMessage> messages = chatService.getAllMessages(cleanToken);
            logger.info("Successfully retrieved all messages");
            return messages;
        } catch (Exception e) {
            logger.error("Failed to get all messages", e);
            throw new ResourceNotFoundException("Failed to get messages: " + e.getMessage());
        }
    }
    //to post messages to DB and notify receiver via WebSocket
    @PostMapping("/send") 
    public ChatMessage sendMessage(@RequestBody ChatMessage message, @RequestHeader("Authorization") String token) {
        logger.info("Received request to send message");
        try {
            if (message == null) {
                throw new IllegalArgumentException("Message cannot be null");
            }
            String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            ChatMessage savedMessage = chatService.saveMessage(message, cleanToken);
            
            // Send message to WebSocket to notify receiver
            chatService.notifyReceiver(savedMessage);
            
            logger.info("Successfully sent and saved message");
            return savedMessage;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid message data provided", e);
            throw new ResourceNotFoundException("Invalid message data: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to send message", e);
            throw new ResourceNotFoundException("Failed to send message: " + e.getMessage());
        }
    }
    //to get messages between users
    @GetMapping("/messages/{sender}/{receiver}")
    public List<ChatMessage> getMessagesBetweenUsers(@PathVariable String sender, @PathVariable String receiver, @RequestHeader("Authorization") String token) {
        logger.info("Received request to get messages between users: {} and {}", sender, receiver);
        try {
            if (sender == null || receiver == null) {
                throw new IllegalArgumentException("Sender and receiver cannot be null");
            }
            String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            List<ChatMessage> messages = chatService.getMessagesBySenderAndReceiver(sender, receiver, cleanToken);
            logger.info("Successfully retrieved messages between users");
            return messages;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid sender or receiver provided", e);
            throw new ResourceNotFoundException("Invalid user data: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to get messages between users", e);
            throw new ResourceNotFoundException("Failed to get messages: " + e.getMessage());
        }
    }
    @MessageMapping("/chat") // Clients send messages here
    @SendTo("/queue/messages") // Messages are broadcasted here
    public ChatMessage sendMessage(ChatMessage message) {
        return message; // Sends back message to subscribers
    }

    
}