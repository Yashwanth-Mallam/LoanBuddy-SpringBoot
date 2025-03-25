package com.loanbuddy.loanbuddy.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.loanbuddy.loanbuddy.model.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

} 