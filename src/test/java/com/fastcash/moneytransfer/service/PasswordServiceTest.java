package com.fastcash.moneytransfer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class PasswordServiceTest {
	
	@Test
    public void testGenerateStrongPassword() {
		PasswordService passwordService = new PasswordService();
		
        String password1 = passwordService.generateStrongPassword();
        String password2 = passwordService.generateStrongPassword();
        
        // Check that the passwords are not null
        assertNotNull(password1);
        assertNotNull(password2);

        // Check that the passwords are not equal
        assertNotEquals(password1, password2);

        // Check that the passwords have the expected length (32 characters for a 24-byte Base64 encoded string)
        assertEquals(32, password1.length());
        assertEquals(32, password2.length());
    }
	
	@Test
    public void testGenerateUniqueStrongPasswords() {
        PasswordService passwordService = new PasswordService();
        Set<String> generatedPasswords = new HashSet<>();
        int numberOfPasswords = 1000;

        for (int i = 0; i < numberOfPasswords; i++) {
            String password = passwordService.generateStrongPassword();
            generatedPasswords.add(password);
        }

        assertEquals(numberOfPasswords, generatedPasswords.size());
    }
	
}
