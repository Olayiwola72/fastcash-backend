package com.fastcash.moneytransfer.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.fastcash.moneytransfer.model.PasswordResetToken;
import com.fastcash.moneytransfer.model.User;

@DataJpaTest
class PasswordResetTokenRepositoryTest {

	@Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Autowired
    private UserRepository userRepository;
	
	private final String token = "token";
	private final String email = "testuser@email.com";
	private final LocalDateTime expiryDate = LocalDateTime.now();
	
	private User user;
	private PasswordResetToken passwordResetToken;
	
	@BeforeEach
	void setUp() {
		
		user = new User(email, "testpassword");
        userRepository.save(user);
        
		passwordResetToken = new PasswordResetToken(token, user, expiryDate);
		passwordResetTokenRepository.save(passwordResetToken);
	}

    @Test
    public void testFindByToken() {
        // When
        PasswordResetToken result = passwordResetTokenRepository.findByToken(token).get();

        // Then
        assertNotNull(passwordResetToken.getId());
		assertEquals(token, result.getToken());
		assertEquals(user, result.getUser());
		assertEquals(expiryDate, result.getExpiryDate());
    }

    @Test
    public void testFindByTokenNotFound() {
        // When
    	Optional<PasswordResetToken> result = passwordResetTokenRepository.findByToken("invalid_token");

        // Then
        assertFalse(result.isPresent());
    }

}
