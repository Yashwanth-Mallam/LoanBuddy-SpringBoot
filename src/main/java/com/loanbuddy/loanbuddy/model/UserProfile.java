package com.loanbuddy.loanbuddy.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "user_profiles")
public class UserProfile {
    @Id
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private String profilePicture;
    private String about;
    private String occupation;
    private String income;
    private String creditScore;
    private String creditHistory;
    private String loanCapacity;

    // Link User
    @DBRef
    private User user;
}
