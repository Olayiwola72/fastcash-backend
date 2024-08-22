package com.fastcash.moneytransfer.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.thymeleaf.TemplateEngine;

import com.fastcash.moneytransfer.config.MessageSourceConfig;
import com.fastcash.moneytransfer.config.PasswordConfig;
import com.fastcash.moneytransfer.service.AccountService;
import com.fastcash.moneytransfer.service.InternalAccountService;
import com.fastcash.moneytransfer.service.InternalChargeAccountService;
import com.fastcash.moneytransfer.service.UserService;
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;
import com.fastcash.moneytransfer.validation.UserValidator;

@DataJpaTest
@Import({
	TokenAuthenticationProvider.class,
	UserService.class,
	AccountService.class,
	InternalAccountService.class,
	InternalChargeAccountService.class,
	PasswordConfig.class,
	MessageSourceConfig.class,
	UserValidator.class,
	DefaultUserDetailsChecker.class,
	EmailNotificationService.class,
	TemplateEngine.class,
})
class TokenAuthenticationProviderTest {

	@Mock
	private Authentication authentication;
	
	@Mock
    private UserDetails userDetails;
	
	@MockBean
    private JavaMailSender javaMailSender;
	
	private TokenAuthenticationProvider tokenAuthenticationProvider;
	
	private UserService userService;
	
	private String username;
	
	private DefaultUserDetailsChecker userDetailsChecker;
	
	@BeforeEach
    void setUp() {
		username = "test@rmail.com";
		userService = mock(UserService.class);
		userDetailsChecker = mock(DefaultUserDetailsChecker.class);
		tokenAuthenticationProvider = new TokenAuthenticationProvider(userService, userDetailsChecker);
    }
    
    @Test
	void testAuthenticate(){  
        when(authentication.getName()).thenReturn(username);
        when(userService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(null);  // Adjust as necessary for your authorities

        Authentication result = tokenAuthenticationProvider.authenticate(authentication);
        
        assertNotNull(result);
        assertTrue(result instanceof UsernamePasswordAuthenticationToken);
        assertEquals(userDetails, result.getPrincipal());
	}
    
	
	@Test
	void testAuthenticateUser(){
        when(userService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(null);  // Adjust as necessary for your authorities

        Authentication result = tokenAuthenticationProvider.authenticateUser(username);
        
        verify(userDetailsChecker, times(1)).check(userDetails);
        
        assertNotNull(result);
        assertTrue(result instanceof UsernamePasswordAuthenticationToken);
        assertEquals(userDetails, result.getPrincipal());
	}
	
    @Test
    public void testSupports() {
        assertTrue(tokenAuthenticationProvider.supports(UsernamePasswordAuthenticationToken.class));
    }
}
