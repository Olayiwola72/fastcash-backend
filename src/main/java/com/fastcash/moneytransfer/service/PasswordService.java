package com.fastcash.moneytransfer.service;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class PasswordService {
	
    private final SecureRandom secureRandom;

    public PasswordService() {
        this.secureRandom = new SecureRandom();
    }

    // Method to generate a strong random password
    public String generateStrongPassword() {
        byte[] bytes = new byte[24]; // 24 bytes => 192 bits
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    
}
