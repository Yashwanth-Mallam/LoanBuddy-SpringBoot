package com.loanbuddy.loanbuddy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.loanbuddy.loanbuddy.model.UserProfile;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
    UserProfile findByUserId(String userId); // Fetch user profile by user ID
}
