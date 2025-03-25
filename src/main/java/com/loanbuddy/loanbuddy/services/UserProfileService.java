package com.loanbuddy.loanbuddy.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loanbuddy.loanbuddy.Exceptions.ResourceNotFoundException;
import com.loanbuddy.loanbuddy.model.User;
import com.loanbuddy.loanbuddy.model.UserProfile;
import com.loanbuddy.loanbuddy.repository.UserProfileRepository;
import com.loanbuddy.loanbuddy.repository.UserRepository;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private String userId;

    // Fetch User Profile by User ID
    public UserProfile getUserProfileByUserId(String userId) {
        return userProfileRepository.findByUserId(userId);
    }

    //service to create a profile
    public UserProfile createUserProfile(String userId, UserProfile userProfile) {
        // Check if user exists in the database
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    
        // Associate the user with the profile
        userProfile.setUser(user);
    
        // Save the user profile to the database
        return userProfileRepository.save(userProfile);
    }
    

    // Get all User Profiles
    public List<UserProfile> getAllUserProfiles() {
        return userProfileRepository.findAll();
    }

    // Delete User Profile by User ID
    public void deleteUserProfile(String userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId);
        if (userProfile == null) {
            throw new ResourceNotFoundException("User profile not found with ID: " + userId);
        }
    }
}
