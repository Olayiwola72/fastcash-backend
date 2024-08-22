package com.fastcash.moneytransfer.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.fastcash.moneytransfer.model.Admin;

@DataJpaTest
class AdminRepositoryTest {

	@Autowired
    private AdminRepository adminRepository;
	
	private final String email = "testuser@email.com";
	private final String testpassword = "testpassword";
	private final String roles = "ADMIN";
	private Admin admin;
	
	@BeforeEach
	void setUp() {
        admin = new Admin();
        admin.setEmail(email);
        admin.setPassword(testpassword);
        admin.setRoles(roles);
        adminRepository.save(admin);
	}

    @Test
    public void testFindByEmail() {
        // When
        Optional<Admin> foundUser = adminRepository.findByEmail(email);

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(email, foundUser.get().getEmail());
        assertEquals(testpassword, foundUser.get().getPassword());
        assertEquals(roles, foundUser.get().getRoles());
    }

    @Test
    public void testFindByEmailNotFound() {
        // When
        Optional<Admin> foundUser = adminRepository.findByEmail("nonexistentemail@email");

        // Then
        assertFalse(foundUser.isPresent());
    }

}
