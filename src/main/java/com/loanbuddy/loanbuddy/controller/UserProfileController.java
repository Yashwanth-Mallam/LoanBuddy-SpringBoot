package com.loanbuddy.loanbuddy.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loanbuddy.loanbuddy.Exceptions.ResourceNotFoundException;
import com.loanbuddy.loanbuddy.model.UserProfile;
import com.loanbuddy.loanbuddy.services.UserProfileService;

@RestController
@RequestMapping("/api/")
public class UserProfileController {
    @Autowired
    private UserProfileService userProfileService;
    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    
    //to get a profile by user id
    @GetMapping("/userprofile/{userId}")
    public UserProfile getUserProfileById(@PathVariable String userId) {
        logger.info("Received request for GET user profile endpoint for userId: {}", userId);
        try {
            return userProfileService.getUserProfileByUserId(userId);
        } catch (Exception e) {
            logger.error("Failed to GET user profile for userId: {}", userId, e);
            throw new ResourceNotFoundException("Failed to get user profile: " + e.getMessage());
        }
    }

    //to create a profile
    @PostMapping("/userprofile/{userId}")
    public UserProfile createUserProfile(@PathVariable String userId, @RequestBody UserProfile userProfile) {
        logger.info("Received request for POST user profile endpoint for userId: {}", userId);
        try {
            UserProfile createdProfile = userProfileService.createUserProfile(userId, userProfile);
            logger.info("User profile created successfully for userId: {}", userId);
            return createdProfile;
        } catch (Exception e) {
            logger.error("Failed to POST user profile for userId: {}", userId, e);
            throw new ResourceNotFoundException("Failed to create user profile: " + e.getMessage());
        }
    }
    //to get all profiles
    @GetMapping("/userprofiles")
    public List<UserProfile> getAllUserProfiles() {
        logger.info("Received request for GET all user profiles endpoint");
        try {
            List<UserProfile> profiles = userProfileService.getAllUserProfiles();
            logger.info("Successfully retrieved all user profiles");
            return profiles;
        } catch (Exception e) {
            logger.error("Failed to GET all user profiles", e);
            throw new ResourceNotFoundException("Failed to get all user profiles: " + e.getMessage());
        }
    }

    //to delete a profile
    @DeleteMapping("/userprofile/{userId}")
    public ResponseEntity<?> deleteUserProfile(@PathVariable String userId) {
        logger.info("Received request for DELETE user profile endpoint for userId: {}", userId);
        try {
            // Check if profile exists
            UserProfile existingProfile = userProfileService.getUserProfileByUserId(userId);
            if (existingProfile == null) {
                logger.warn("User profile not found for userId: {}", userId);
                return ResponseEntity.notFound().build();
            }

            userProfileService.deleteUserProfile(userId);
            logger.info("Successfully deleted user profile for userId: {}", userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to DELETE user profile for userId: {}", userId, e);
            throw new ResourceNotFoundException("Failed to delete user profile: " + e.getMessage());
        }
    }
        

}
