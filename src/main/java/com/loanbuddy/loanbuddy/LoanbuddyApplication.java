package com.loanbuddy.loanbuddy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoanbuddyApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(LoanbuddyApplication.class, args);
	}

	@Override
	public void run(String... args) {
		// this will log the info the the app is running.
		System.out.println("Application started!");
	}

	
}
