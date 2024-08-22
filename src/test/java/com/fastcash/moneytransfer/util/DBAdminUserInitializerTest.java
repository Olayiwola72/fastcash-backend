package com.fastcash.moneytransfer.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;

import com.fastcash.moneytransfer.config.MessageSourceConfig;
import com.fastcash.moneytransfer.config.PasswordConfig;
import com.fastcash.moneytransfer.enums.AuthMethod;
import com.fastcash.moneytransfer.enums.UserType;
import com.fastcash.moneytransfer.model.Admin;
import com.fastcash.moneytransfer.repository.AdminRepository;
import com.fastcash.moneytransfer.service.AccountService;
import com.fastcash.moneytransfer.service.InternalAccountService;
import com.fastcash.moneytransfer.service.InternalChargeAccountService;
import com.fastcash.moneytransfer.service.UserService;
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;
import com.fastcash.moneytransfer.validation.UserValidator;

@DataJpaTest
@ContextConfiguration(classes = TestConfig.class)
@Import({ 
	UserService.class,
	PasswordConfig.class,
	MessageSourceConfig.class,
	UserValidator.class,
	AccountService.class,
	InternalAccountService.class,
	InternalChargeAccountService.class,
	EmailNotificationService.class,
	TemplateEngine.class,
})
class DBAdminUserInitializerTest {
	
	@Value("${app.admin.email}") 
	private String adminEmail;
	
	@Value("${app.admin.password}") 
	private String adminPassword;
	
	@Value("${app.admin.user.roles}")
	private String roles;
	
	@Value("${spring.application.name}")
	private String applicationName;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AdminRepository adminRepository;
	
	@Autowired 
	private PasswordEncoder passwordEncoder;
	
	@MockBean
    private JavaMailSender javaMailSender;
	
	@Test
	void testAdminUserCreation() {
		Admin admin = null;
		
		new DBAdminUserInitializer(adminEmail, adminPassword, roles, applicationName, userService, adminRepository);
		
		Optional<Admin> optionalAdmin = adminRepository.findByEmail(adminEmail);
		if(optionalAdmin.isPresent()) {
			admin = optionalAdmin.get();
		}
		 
    	// Perform assertions based on the expected data in the database
        assertNotNull(admin); 
        assertEquals(adminEmail, admin.getEmail());
        assertTrue(passwordEncoder.matches(adminPassword, admin.getPassword())); // Password should remain unchanged
        assertEquals(roles, admin.getRoles());
        assertTrue(admin.isEnabled());
        assertEquals(AuthMethod.ADMIN, admin.getAuthMethod());
        assertEquals(UserType.INTERNAL, admin.getUserType());
        assertEquals(applicationName, admin.getName());
        assertEquals(0,admin.getAccountStatements().size());
        
        // Act and Assert
        assertDoesNotThrow(() -> {
        	new DBAdminUserInitializer(adminEmail, adminPassword, roles, applicationName, userService, adminRepository);
    		
            // no change was made to the optionalAdminUser created earlier and a duplicate admin user was not created
            assertEquals(optionalAdmin.get(), adminRepository.findByEmail(adminEmail).get()); ;
        });
	}
}
