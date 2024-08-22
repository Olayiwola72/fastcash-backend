package com.fastcash.moneytransfer.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import com.fastcash.moneytransfer.config.MessageSourceConfig;
import com.fastcash.moneytransfer.config.PasswordConfig;
import com.fastcash.moneytransfer.exception.UsernameAlreadyExistsException;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.service.AccountService;
import com.fastcash.moneytransfer.service.InternalAccountService;
import com.fastcash.moneytransfer.service.InternalChargeAccountService;
import com.fastcash.moneytransfer.service.UserService;
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;

@DataJpaTest
@Import({
	UserService.class,
	AccountService.class,
	InternalAccountService.class,
	InternalChargeAccountService.class,
	PasswordConfig.class,
	MessageSourceConfig.class,
	UserValidator.class,
	EmailNotificationService.class,
	TemplateEngine.class,
})
class ExistingUsernameValidatorTest {
	
	@Autowired
	private UserService userService;
	
	@MockBean
    private JavaMailSender javaMailSender;
	
	private ExistingUsernameValidator validator;
	
	private String username;
	
	@BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new ExistingUsernameValidator(userService);
        username = "new@email.com";
    }
	
	@Test
	void testIsEmailExisting_WithAUniqueUsername() {
		assertDoesNotThrow(() -> validator.isEmailExisting("test@uniqueemail.com"));
	}
	
	@Test
	void testIsEmailExisting_WithAnExisthingUsername() {
		User user = new User(username, "password");
		userService.create(user);
		
		assertThrows(UsernameAlreadyExistsException.class, () -> validator.isEmailExisting(username));
	}
}
