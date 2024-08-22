package com.fastcash.moneytransfer.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.model.User;

class APIResponseTest {
	
	private final String successMessage = "Signed in successfully";
	private final String token = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic.3ViIjoi";
	private final String refreshToken = "03963737-e8a3-4063-9053-a76d5dfb1d05";
	private final UserResponse userResponse = new UserResponse(new User());
	
	@Test
	void testUserCreationResponseDefaultConstructor() {
		APIResponse userCreationResponse = new APIResponse(successMessage, token, refreshToken, userResponse);
		
		assertEquals(successMessage, userCreationResponse.successMessage());
		assertEquals(token, userCreationResponse.token());
		assertEquals(refreshToken, userCreationResponse.refreshToken());
		assertEquals(userResponse, userCreationResponse.userData());
	}
}
