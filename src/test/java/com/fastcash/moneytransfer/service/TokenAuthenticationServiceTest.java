package com.fastcash.moneytransfer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
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
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;
import com.fastcash.moneytransfer.util.KeyPairFileUtil;
import com.fastcash.moneytransfer.util.RSAKeyPairGenerator;
import com.fastcash.moneytransfer.util.TestConfig;
import com.fastcash.moneytransfer.validation.UserTypeChecker;
import com.fastcash.moneytransfer.validation.UserValidator;

@DataJpaTest
@ContextConfiguration(classes = TestConfig.class)
@Import({
	UserService.class,
	InternalAccountService.class,
	InternalChargeAccountService.class,
	PasswordConfig.class,
	MessageSourceConfig.class,
	TokenAuthenticationService.class,
	SecurityConfig.class,
	RsaKeyConfig.class,
	RSAKeyPairGenerator.class,
	KeyPairFileUtil.class,
	DelegatedAuthenticationEntryPoint.class, 
	DelegatedBearerTokenAccessDeniedHandler.class,
	UserValidator.class,
	InternalExternalUserFilter.class,
	TokenAuthenticationProvider.class,
	UserTypeChecker.class,
	AccountService.class,
	UserUpdateAuthorizationManager.class,
	AccountUpdateAuthorizationManager.class,
	DefaultUserDetailsChecker.class,
	EmailNotificationService.class,
	TemplateEngine.class
})
class TokenAuthenticationServiceTest {
	
	@Autowired
	private JwtDecoder jwtDecoder;

    @Autowired
    private UserService userService;
    
    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;
    
    @Mock
    private TokenAuthenticationProvider tokenAuthenticationProvider;
    
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ApplicationEventPublisher eventPublisher;
    
    @MockBean
    private JavaMailSender javaMailSender;
    
    @Mock
    private TemplateEngine mockTemplateEngine;

    @MockBean
    private EmailNotificationService emailNotificationService;
    
    private User user;
    
    private String username;
    
    @Value("${jwt.expires.in.hours}") 
    private long jwtExpiresIn;
    
    private String issuer = "http://self";
	
    @BeforeEach
    void setUp() {
    	username = "test@email.com";

		user = new User(username, "password");
		user.setRoles("USER ADMIN");
        userService.create(user);
		
		MockitoAnnotations.openMocks(this);
    }
    
    @AfterEach
    void tearDown() {
		userService.deleteById(user.getId());
    }
    
    @AfterAll
    static void tearOut() {
		SecurityContextHolder.clearContext();
    }
    
    @Test
    void testGenerateTokenWithAuthentication() {
    	// Arrange
    	UserDetails userDetails = userService.loadUserByUsername(username);
    	
    	UsernamePasswordAuthenticationToken authentication = 
    			new UsernamePasswordAuthenticationToken(
                		 userDetails, 
                		 userDetails.getPassword(), 
                		 userDetails.getAuthorities());

        // Act
        String token = tokenAuthenticationService.generateToken(authentication, user);
		Jwt jwt = jwtDecoder.decode(token);
		
        // Assert
        assertNotNull(token);
        assertTrue(tokenAuthenticationService.validateJwtToken(token));
        assertEquals(issuer, jwt.getIssuer().toString());
        assertEquals(user.getEmail(), tokenAuthenticationService.getUserNameFromJwtToken(token));
        assertEquals(jwt.getExpiresAt().minus(jwtExpiresIn, ChronoUnit.HOURS), jwt.getIssuedAt());
		assertEquals(
			jwt.getClaim("authorities"),
			tokenAuthenticationService.getAuthoritiesAsString(authentication.getAuthorities())
	    );
    }
    
    @Test
    public void testAuthenticateUser_WithUsername() {
        // Mock HttpServletRequest for passing as argument
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1"); // Example IP address
        
        // Create a UserDetails object to be returned by the mocked userService
        UserDetails userDetails = userService.loadUserByUsername(username);
        
        // Mock the authentication provided by tokenAuthenticationProvider
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        
        // Stub the tokenAuthenticationProvider.authenticateUser method
        Mockito.when(tokenAuthenticationProvider.authenticateUser(username))
               .thenReturn(authentication);
        
     // Initialize TokenAuthenticationService with mocked dependencies
        TokenAuthenticationService mockTokenAuthenticationService = new TokenAuthenticationService(
            24L, // jwtExpiresIn (example value, adjust as necessary)
            mock(JwtEncoder.class),
            mock(JwtDecoder.class),
            authenticationManager,
            tokenAuthenticationProvider, 
            eventPublisher,
            emailNotificationService
        );
        
        // Call the method under test
        Authentication returnedAuthentication = mockTokenAuthenticationService.authenticateUser(username, request);
        
        // Verify assertions
        assertTrue(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(returnedAuthentication, SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
        assertEquals(userDetails.getAuthorities(), SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        
        // Verify event publication
        Mockito.verify(eventPublisher, Mockito.times(1)).publishEvent(Mockito.any(AuthenticationSuccessEvent.class));
    }
    
    @Test
    void testAuthenticateUser_WithUsernameAndPassword() {
    	String token = tokenAuthenticationService.authenticateUser(username, "password");

        // Verify that the authentication was successful
        assert SecurityContextHolder.getContext().getAuthentication() != null;
        assert SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
        assert SecurityContextHolder.getContext().getAuthentication().getName().equals(username);
        assert token != null;
    }
    
}
