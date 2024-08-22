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

import com.fastcash.moneytransfer.model.RefreshToken;
import com.fastcash.moneytransfer.model.User;

@DataJpaTest
class RefreshTokenRepositoryTest {

	@Autowired
    private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
    private UserRepository userRepository;
	
	private final String token = "token";
	private final String email = "testuser@email.com";
	private final LocalDateTime expiryDate = LocalDateTime.now();
	private final String userAgent = "userAgent";
	
	private User user;
	private RefreshToken refreshToken;
	
	@BeforeEach
	void setUp() {
		user = new User(email, "testpassword");
        userRepository.save(user);
        
		refreshToken = new RefreshToken(token, expiryDate, user, userAgent);
		refreshTokenRepository.save(refreshToken);
	}

    @Test
    void testFindByToken() {
        // When
    	RefreshToken result = refreshTokenRepository.findByToken(token).get();

        // Then
        assertNotNull(result.getId());
		assertEquals(token, result.getToken());
		assertEquals(user, result.getUser());
		assertEquals(expiryDate, result.getExpiryDate());
    }

    @Test
    void testFindByTokenNotFound() {
        // When
    	Optional<RefreshToken> result = refreshTokenRepository.findByToken("invalid_token");

        // Then
        assertFalse(result.isPresent());
    }
    
    @Test
    void testFindByUserAndUserAgent() {
        // When
    	RefreshToken result = refreshTokenRepository.findByUserAndUserAgent(user, userAgent).get();

        // Then
        assertNotNull(result.getId());
		assertEquals(token, result.getToken());
		assertEquals(user, result.getUser());
		assertEquals(expiryDate, result.getExpiryDate());
    }
    
    @Test
    void testDeleteByUserAndUserAgent() {
        // When
    	refreshTokenRepository.deleteByUserAndUserAgent(user, userAgent);

        // Then
    	assertFalse(refreshTokenRepository.findByUserAndUserAgent(user, userAgent).isPresent());
    }
    
    @Test
    void testDeleteAllByUser() {
        // When
    	refreshTokenRepository.deleteAllByUser(user);

        // Then
    	assertFalse(refreshTokenRepository.findByUserAndUserAgent(user, userAgent).isPresent());
    }

}
