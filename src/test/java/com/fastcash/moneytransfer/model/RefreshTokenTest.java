package com.fastcash.moneytransfer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RefreshTokenTest {
	
	private final Long id = 0L;
	private final String token = "token";
	private final LocalDateTime expiryDate = LocalDateTime.now();
	private final User user = new User();
	private final String userAgent = "userAgent";
	private RefreshToken refreshToken;
	
	@BeforeEach
	void setUp() {
		refreshToken = new RefreshToken(token, expiryDate, user, userAgent);
	}
	
	@Test
	void testRefreshTokenDefaultInitialization() {
		refreshToken = new RefreshToken();
		
		assertNull(refreshToken.getId()); // Id should be null
		assertNull(refreshToken.getToken()); // token should be null
		assertNull(refreshToken.getExpiryDate()); // expiryDate should be null
		assertNull(refreshToken.getUser()); // user should be null
		assertNull(refreshToken.getUserAgent()); // userAgent should be null
	}
	
	@Test
	void testRefreshTokenCustomInitialization() {
		assertNull(refreshToken.getId()); // Id should be null
		assertNotNull(refreshToken.getToken()); // token should not be null
		assertNotNull(refreshToken.getExpiryDate()); // expiryDate should not be null
		assertNotNull(refreshToken.getUser()); // user should not be null
		assertNotNull(refreshToken.getUserAgent()); // userAgent should not be null
	}
	
	@Test
	void testGettersAndSetters() {
		refreshToken.setId(id);
		
		assertEquals(id, refreshToken.getId());
		assertEquals(token, refreshToken.getToken());
		assertEquals(expiryDate, refreshToken.getExpiryDate());
		assertEquals(user, refreshToken.getUser());
		assertEquals(userAgent, refreshToken.getUserAgent());
	}

}
