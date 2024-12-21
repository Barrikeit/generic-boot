package org.barrikeit;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Password {
    public static void main(String[] args) {
        // Create a PasswordEncoder instance
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        // The plain text password
        String plainPassword = "1234";

        // Encode the password
        String encodedPassword = passwordEncoder.encode(plainPassword);

        // Print the encoded password
        System.out.println("Encoded Password: " + encodedPassword);
    }
}