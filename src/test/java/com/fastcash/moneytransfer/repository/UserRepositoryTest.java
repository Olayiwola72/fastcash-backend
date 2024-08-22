package com.fastcash.moneytransfer.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;

import com.fastcash.moneytransfer.config.MessageSourceConfig;
import com.fastcash.moneytransfer.config.PasswordConfig;
import com.fastcash.moneytransfer.config.RsaKeyConfig;
import com.fastcash.moneytransfer.config.SecurityConfig;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.security.AccountUpdateAuthorizationManager;
import com.fastcash.moneytransfer.security.DefaultUserDetailsChecker;
import com.fastcash.moneytransfer.security.DelegatedAuthenticationEntryPoint;
import com.fastcash.moneytransfer.security.DelegatedBearerTokenAccessDeniedHandler;
import com.fastcash.moneytransfer.security.InternalExternalUserFilter;
import com.fastcash.moneytransfer.security.TokenAuthenticationProvider;
import com.fastcash.moneytransfer.security.UserUpdateAuthorizationManager;
import com.fastcash.moneytransfer.service.AccountService;
import com.fastcash.moneytransfer.service.InternalAccountService;
import com.fastcash.moneytransfer.service.InternalChargeAccountService;
import com.fastcash.moneytransfer.service.TokenAuthenticationService;
import com.fastcash.moneytransfer.service.UserService;
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;
import com.fastcash.moneytransfer.util.KeyPairFileUtil;
import com.fastcash.moneytransfer.util.RSAKeyPairGenerator;
import com.fastcash.moneytransfer.util.TestConfig;
import com.fastcash.moneytransfer.validation.UserTypeChecker;
import com.fastcash.moneytransfer.validation.UserValidator;

@DataJpaTest
@ContextConfiguration(classes = TestConfig.class)
@Import({ 
	SecurityConfig.class,  
	RsaKeyConfig.class,
	RSAKeyPairGenerator.class,
	KeyPairFileUtil.class,
	PasswordConfig.class,
	UserService.class, 
	DelegatedAuthenticationEntryPoint.class, 
	DelegatedBearerTokenAccessDeniedHandler.class,
	MessageSourceConfig.class,
	UserValidator.class,
	InternalExternalUserFilter.class,
	TokenAuthenticationService.class,
	TokenAuthenticationProvider.class,
	UserTypeChecker.class,
	AccountService.class,
	InternalAccountService.class,
	InternalChargeAccountService.class,
	UserUpdateAuthorizationManager.class,
	AccountUpdateAuthorizationManager.class,
	DefaultUserDetailsChecker.class,
	EmailNotificationService.class,
	TemplateEngine.class,
})
class UserRepositoryTest {

	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private AccountService accountService;
	
	@MockBean
    private JavaMailSender mockMailSender;

    @Mock
    private TemplateEngine mockTemplateEngine;

    @MockBean
    private EmailNotificationService emailNotificationService;
	
	private final String email = "testuser@email.com";
	private User user;
	
	@BeforeEach
	void setUp() {
        user = new User(email, "testpassword");
        // Save the user to the repository
        userRepository.save(user);
	}
    
    @Test
    public void testFindByEmailAndDeletedIsFalse() {
        // When
        Optional<User> foundUser = userRepository.findByEmailAndDeletedIsFalse(email);

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(email, foundUser.get().getEmail());
        assertEquals("testpassword", foundUser.get().getPassword());
        assertEquals("USER", foundUser.get().getRoles());
    }
    
    @Test
    public void testFindByEmailAndDeletedIsFalse_isDeletedTrue() {
    	user.setDeleted(true);
    	userRepository.save(user);
        
        // When
        Optional<User> foundUser = userRepository.findByEmailAndDeletedIsFalse(email);

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    public void testFindByEmailNotFound() {
        // When
        Optional<User> foundUser = userRepository.findByEmailAndDeletedIsFalse("nonexistentemail@email");

        // Then
        assertFalse(foundUser.isPresent());
    }
    
    @Test
    public void testfindByAccounts_Id() {
        // Given
        user.setAccounts(accountService.create(user));
        Long accountId = user.getAccounts().get(0).getId();

        // When
        Optional<User> foundUser = userRepository.findByUserAccounts_Id(accountId);
        
        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(email, foundUser.get().getEmail());
    }

}
