package com.loanbuddy.loanbuddy.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loanbuddy.loanbuddy.Exceptions.ResourceNotFoundException;
import com.loanbuddy.loanbuddy.model.User;
import com.loanbuddy.loanbuddy.services.UserService;

@RestController
@RequestMapping("/api/users")    
public class UserController {
    @Autowired
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    //to get the user by  id
    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        logger.info("Received request for GET user endpoint");
        try {
            return userService.getUserById(id);
        } catch (Exception e) {
            logger.error("Failed to GET user with id: " + id, e);
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
    }
    //to get all the users 
    @GetMapping("/")
    public List<User> getAllUsers() {
        logger.info("Received request for GET all users endpoint");
        try {
            return userService.getAllUsers();
        } catch (Exception e) {
            logger.error("Failed to GET all users", e);
            throw new ResourceNotFoundException("Failed to get users: " + e.getMessage());
        }
    }
    // to create tthe user
    @PostMapping("/register")
    public String createUser(@RequestBody User user) {
        try {
            logger.info("Received request for create user endpoint");
            if (user == null) {
                throw new IllegalArgumentException("User cannot be null");
            }
            return userService.createUser(user);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid user data provided", e);
            throw new ResourceNotFoundException("Invalid user data: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to CREATE user", e);
            throw new ResourceNotFoundException("Failed to create user: " + e.getMessage());
        }
    }
    //to login the user
    @PostMapping("/login")
    public String login(@RequestBody User user) {
        try {
            logger.info("Received request for login endpoint");
            if (user == null || user.getEmail() == null || user.getPassword() == null) {
                throw new IllegalArgumentException("Invalid login credentials");
            }
            return userService.login(user);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid login attempt", e);
            throw new ResourceNotFoundException("Login failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to login user", e);
            throw new ResourceNotFoundException("Login failed: " + e.getMessage());
        }
    }
    //to update the user
    @PutMapping("/updateuser/{id}")
    public User updateUser(@PathVariable String id, @RequestBody User user) {
        try {
            logger.info("Received request for update user endpoint");
            // Check if user exists be  fore updating
            User existingUser = userService.getUserById(id);
            if (existingUser == null) {
                logger.error("User not found with id: " + id);
                throw new ResourceNotFoundException("User not found with id: " + id);
            }
            return userService.updateUserDetails(id, user);
        } catch (Exception e) {
            logger.error("Failed to UPDATE user", e);
            throw new ResourceNotFoundException("Failed to update user: " + e.getMessage());
        }
    }
    //to delete the user
    @DeleteMapping("/deleteuser/{id}")
    public String deleteUser(@PathVariable String id) {
        logger.info("Received request for delete user endpoint");
        try {
            userService.deleteUser(id);
            logger.info("User deleted successfully with id: " + id);
            return "User deleted successfully";
        } catch (Exception e) {
            logger.error("Failed to DELETE user with id: " + id, e);
            throw new ResourceNotFoundException("Failed to delete user: " + e.getMessage());
        }
    }
    
}