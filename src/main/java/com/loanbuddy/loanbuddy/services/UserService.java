package com.loanbuddy.loanbuddy.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.loanbuddy.loanbuddy.Exceptions.ResourceNotFoundException;
import com.loanbuddy.loanbuddy.Security.JwtUtil;
import com.loanbuddy.loanbuddy.model.User;
import com.loanbuddy.loanbuddy.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id) {
        return ((Optional<User>) userRepository.findById(id))
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    // public User getUserByEmail(String email) {
    //     User user = userRepository.findByEmail(email);
    //     if (user == null) {
    //         throw new ResourceNotFoundException("User not found with email: " + email);
    //     }
    //     return user;
    // }

    //to register the user
    public String createUser(User user) {
        // ✅ Check if user already exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User already exists with email: " + user.getEmail());
        }

        // ✅ Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // ✅ Save the user in the database
        userRepository.save(user);

        // ✅ Generate JWT Token for the new user
        return jwtUtil.generateToken(user.getEmail());
    }

    //to login the user
    public String login(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (!existingUser.isPresent()) {
            throw new ResourceNotFoundException("User not found with email: " + user.getEmail());
        }

        User foundUser = existingUser.get();

        if (!passwordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
            throw new IllegalArgumentException("Incorrect password");
        }

        // ✅ Generate JWT token upon successful login
        try {
            return jwtUtil.generateToken(foundUser.getEmail());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to generate token: " + e.getMessage());
        }
    }

    public User updateUserDetails(String id, User userDetails) {
        User user = getUserById(id);
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    // public User updateUser(String id, User user) {
    //     throw new UnsupportedOperationException("Not supported yet.");
    // }
    public List<User> getAllLenders() {
        try {
            List<User> lenders = userRepository.findAllByType("lender");
            if (lenders.isEmpty()) {
                throw new ResourceNotFoundException("No lenders found in the system");
            }
            return lenders;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error retrieving lenders: " + e.getMessage());
        }
    }

    public Stream<Object> getUserProfileByEmail(String email) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
