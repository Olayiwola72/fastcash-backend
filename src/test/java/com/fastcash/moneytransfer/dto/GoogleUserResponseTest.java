package com.fastcash.moneytransfer.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.model.User;

class GoogleUserResponseTest {
	
	@Test
	void testGoogleUserResponseDefaultConstructor() {
		User existingUser = new User();
		User googleUser = new User();
		
		GoogleUserResponse googleUserResponse = new GoogleUserResponse(existingUser, googleUser);
		
		assertNotNull(googleUserResponse.googleUser());
		assertNotNull(googleUserResponse.existingUser());
	}
	
}
