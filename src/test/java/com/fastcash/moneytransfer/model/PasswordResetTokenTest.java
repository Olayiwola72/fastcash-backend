package com.fastcash.moneytransfer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class PasswordResetTokenTest {
	
	private final String token = "token";
	private final User user = new User();
	private final LocalDateTime expiryDate = LocalDateTime.now();
	
	@Test
	void testTokenUserAndLocalDateTimeConstructor() {
		PasswordResetToken passwordResetToken = new PasswordResetToken(token, user, expiryDate);
		
		assertNull(passwordResetToken.getId());
		assertEquals(token, passwordResetToken.getToken());
		assertEquals(user, passwordResetToken.getUser());
		assertEquals(expiryDate, passwordResetToken.getExpiryDate());
	}
	
}
