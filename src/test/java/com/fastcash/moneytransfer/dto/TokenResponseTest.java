package com.fastcash.moneytransfer.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class TokenResponseTest {
	
	@Test
	void testTokenResponseDefaultConstructor() {
		TokenResponse tokenResponse = new TokenResponse("eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic.3ViIjoi", "f0bf0001-649d-49c5-b0e5-d68f6bab583a");
		
		assertNotNull(tokenResponse.token());
		assertNotNull(tokenResponse.refreshToken());
	}
}
