package com.fastcash.moneytransfer.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.fastcash.moneytransfer.exception.UserAccountMismatchException;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.repository.UserAccountRepository;
import com.fastcash.moneytransfer.repository.UserRepository;

@DataJpaTest
class UserAccountMismatchValidatorTest {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserAccountRepository userAccountRepository;
	
	private User user;
	
	private UserAccount userAccount;
	
	@BeforeEach
    void setUp() {
        userAccount = new UserAccount();
        
        user = new User("user@example.com", "password");
		user.setAccounts(List.of(userAccount));
		userRepository.save(user);
    }
	
	@Test
	void testHandleMismatch_WithValidUserAccount() {
		assertDoesNotThrow(() -> UserAccountMismatchValidator.handleMismatch(userAccount, user, "debitAccount"));
	}
	
	@Test
	void testHandleMismatch_WithMimatchAccount() {		
		userAccount = new UserAccount();
		userAccountRepository.save(userAccount);
		
		assertThrows(UserAccountMismatchException.class, () -> UserAccountMismatchValidator.handleMismatch(userAccount, user, "debitAccount"));
	}
}
