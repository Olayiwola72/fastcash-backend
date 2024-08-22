package com.fastcash.moneytransfer.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.fastcash.moneytransfer.model.BaseUser;
import com.fastcash.moneytransfer.model.User;

@DataJpaTest
class BaseUserRepositoryTest {

	@Autowired
    private BaseUserRepository baseUserRepository;
	
	private final String email = "testuser@email.com";
	private User user;
	
	@BeforeEach
	void setUp() {
        user = new User(email, "testpassword");
        user.setDeleted(false);
        // Save the user to the repository
        baseUserRepository.save(user);
	}

    @Test
    public void testFindByEmailAndDeletedIsFalse() {
        // When
        Optional<BaseUser> foundUser = baseUserRepository.findByEmailAndDeletedIsFalse(email);

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(email, foundUser.get().getEmail());
        assertEquals("testpassword", foundUser.get().getPassword());
        assertEquals("USER", foundUser.get().getRoles());
    }
    
    @Test
    public void testFindByEmailAndDeletedIsFalse_isDeletedTrue() {
    	user.setDeleted(true);
        baseUserRepository.save(user);
        
        // When
        Optional<BaseUser> foundUser = baseUserRepository.findByEmailAndDeletedIsFalse(email);

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    public void testFindByEmailNotFound() {
        // When
        Optional<BaseUser> foundUser = baseUserRepository.findByEmailAndDeletedIsFalse("nonexistentemail@email");

        // Then
        assertFalse(foundUser.isPresent());
    }

}
