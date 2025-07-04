package com.loanbuddy.loanbuddy.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.loanbuddy.loanbuddy.model.ChatMessage;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    public ChatMessage save(ChatMessage message);

    public List<ChatMessage> findBySenderAndReceiver(String sender, String receiver);
}