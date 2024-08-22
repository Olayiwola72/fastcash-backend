package com.fastcash.moneytransfer.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.repository.UserAccountRepository;

import jakarta.validation.ConstraintValidatorContext;

@DataJpaTest
class ValidAccountValidatorTest {
	
	@Autowired
    private UserAccountRepository userAccountRepository;
	
	@Autowired
    private TestEntityManager entityManager;

    @Mock
    private ConstraintValidatorContext context;
    
    private ValidAccountValidator validator;
    
    private UserAccount userAccount;
    
    
    @BeforeEach
    void setUp() {
        validator = new ValidAccountValidator(userAccountRepository);
        
        User user = new User("user@example.com", "password");
        entityManager.persist(user);
        entityManager.flush();
        
        userAccount = new UserAccount(Currency.NGN, user); 
    }

    @Test
    void testIsValid_WithValidAccountt() {
        // Arrange
        userAccount = userAccountRepository.save(userAccount);
        
        // Act
        boolean isValid = validator.isValid(userAccount.getId(), context);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testIsValid_WithNonExistingAccount() {
        // Arrange
    	userAccount.setId(1L); // Missing UserAccount
    	
        // Act
        boolean isValid = validator.isValid(userAccount.getId(), context);

        // Assert
        assertFalse(isValid);
    }
    
    @Test
    void testIsValid_WithNullValue() {
        // Act
        boolean isValid = validator.isValid(userAccount.getId(), context); // userAccount.getId() is null

        // Assert
        assertTrue(isValid);
    }
    
}
