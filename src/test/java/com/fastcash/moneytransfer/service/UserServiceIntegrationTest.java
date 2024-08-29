package com.fastcash.moneytransfer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.TemplateEngine;

import com.fastcash.moneytransfer.config.MessageSourceConfig;
import com.fastcash.moneytransfer.config.PasswordConfig;
import com.fastcash.moneytransfer.config.RestTemplateConfig;
import com.fastcash.moneytransfer.config.RsaKeyConfig;
import com.fastcash.moneytransfer.config.SecurityConfig;
import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.dto.ExternalAccountRequestMapper;
import com.fastcash.moneytransfer.dto.MoneyTransferRequest;
import com.fastcash.moneytransfer.dto.MoneyTransferRequestMapper;
import com.fastcash.moneytransfer.enums.AuthMethod;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionDirection;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fastcash.moneytransfer.enums.UserType;
import com.fastcash.moneytransfer.model.AccountStatement;
import com.fastcash.moneytransfer.model.Admin;
import com.fastcash.moneytransfer.model.MoneyTransfer;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.repository.AdminRepository;
import com.fastcash.moneytransfer.repository.UserRepository;
import com.fastcash.moneytransfer.security.AccountUpdateAuthorizationManager;
import com.fastcash.moneytransfer.security.DefaultUserDetailsChecker;
import com.fastcash.moneytransfer.security.DelegatedAuthenticationEntryPoint;
import com.fastcash.moneytransfer.security.DelegatedBearerTokenAccessDeniedHandler;
import com.fastcash.moneytransfer.security.InternalExternalUserFilter;
import com.fastcash.moneytransfer.security.TokenAuthenticationProvider;
import com.fastcash.moneytransfer.security.UserUpdateAuthorizationManager;
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;
import com.fastcash.moneytransfer.service.impl.ExchangeRateServiceImpl;
import com.fastcash.moneytransfer.util.KeyPairFileUtil;
import com.fastcash.moneytransfer.util.RSAKeyPairGenerator;
import com.fastcash.moneytransfer.util.TestConfig;
import com.fastcash.moneytransfer.util.UUIDTimestampTransactionIdGenerator;
import com.fastcash.moneytransfer.validation.UserTypeChecker;
import com.fastcash.moneytransfer.validation.UserValidator;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@DataJpaTest
@Import({
	RestTemplateConfig.class,
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
	UUIDTimestampTransactionIdGenerator.class,
	ExchangeRateServiceImpl.class,
	InternalExternalUserFilter.class,
	TokenAuthenticationProvider.class,
	UserTypeChecker.class,
	AccountService.class,
	MoneyTransferRequestMapper.class,
	ExternalAccountService.class,
	UserTypeChecker.class,
	ExternalAccountRequestMapper.class,
	AccountUpdateAuthorizationManager.class,
	UserUpdateAuthorizationManager.class,
	DefaultUserDetailsChecker.class,
	EmailNotificationService.class,
	TemplateEngine.class,
})
class UserServiceIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserService userService;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
	private MoneyTransferRequestMapper moneyTransferRequestMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private InternalAccountService internalAccountService;

    @Mock
    private InternalChargeAccountService internalChargeAccountService;
    
    @MockBean
    private JavaMailSender javaMailSender;
    
    @Mock
    private TemplateEngine mockTemplateEngine;

    @MockBean
    private EmailNotificationService emailNotificationService;
    
	private User user;
	
	private UserAccount debitAccount;
	
	private UserAccount creditAccount;
	
	@BeforeEach
	void setUp() {
        user = new User("user@example.com", "password");
        user.setRoles("USER");
        entityManager.persist(user);
        
        debitAccount = new UserAccount(Currency.NGN, user);
        entityManager.persist(debitAccount);
        
        creditAccount = new UserAccount(Currency.USD, user);
        entityManager.persist(creditAccount);
        
        entityManager.flush();
	}
	
	@AfterEach
	void tearDown() {
		userRepository.delete(user);
		SecurityContextHolder.clearContext();
	}

    @Test
    void testFindAll() {
        List<User> result = userService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void testFindById() {
        Optional<User> optionalUser = userService.findById(user.getId());

        assertTrue(optionalUser.isPresent());
        assertEquals(user.getEmail(), optionalUser.get().getEmail());
        
        // Verify that collections are initialized
        assertTrue(Hibernate.isInitialized(optionalUser.get().getAccounts()));
        assertTrue(Hibernate.isInitialized(optionalUser.get().getTransfers()));
        assertTrue(Hibernate.isInitialized(optionalUser.get().getAccountStatements()));
    }
    
    @Test
    void testFindById_ObjectNotFoundException() {
        assertThrows(ObjectNotFoundException.class, () -> {
        	userService.findById(1L);
        });
    }

    @Test
    void testFindByEmail() {
        Optional<User> optionalUser = userService.findByEmail("user@example.com");

        assertTrue(optionalUser.isPresent());
        assertEquals(user.getEmail(), optionalUser.get().getEmail());
        
        // Verify that collections are initialized
        assertTrue(Hibernate.isInitialized(optionalUser.get().getAccounts()));
        assertTrue(Hibernate.isInitialized(optionalUser.get().getTransfers()));
        assertTrue(Hibernate.isInitialized(optionalUser.get().getAccountStatements()));
    }
    
    @Test
    void testFindById_UsernameNotFoundException() {
        assertThrows(UsernameNotFoundException.class, () -> {
        	userService.findByEmail("noemail@nothing.com");
        });
    }
    
    @Test
    void testFindAdminByEmail() {
        Admin admin = new Admin();
        admin.setEmail("admin@example.com");
        admin.setPassword("password");
        admin.setRoles("ADMIN");
        entityManager.persist(admin);
        entityManager.flush();

        Optional<Admin> optionalAdmin = userService.findAdminByEmail("admin@example.com");

        assertTrue(optionalAdmin.isPresent());
        assertEquals(admin.getEmail(), optionalAdmin.get().getEmail());
    }

    @Test
    void testCreate_WithUser() {
        User user = new User("user@create.com", "password");

        User savedUser = userService.create(user);

        assertNotNull(savedUser);
        assertTrue(userService.findById(savedUser.getId()).isPresent());
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getPassword(), savedUser.getPassword());
        assertEquals(Currency.values().length, savedUser.getAccounts().size());
    }
    
    @Test
    void testCreate_WithAdmin() {
        Admin admin = new Admin();
        admin.setEmail("admin@withAdmin.com");
        admin.setPassword("password");
        admin.setRoles("ADMIN");

        Admin savedAdmin = userService.create(admin);

        assertNotNull(savedAdmin);
        assertTrue(userService.findAdminByEmail(savedAdmin.getEmail()).isPresent());
        assertEquals(admin.getEmail(), savedAdmin.getEmail());
        assertEquals(admin.getPassword(), savedAdmin.getPassword());
        
        adminRepository.delete(savedAdmin);
    }
    
    @Test
    public void testUpdate_WithAdmin() {
    	 // Create input values
    	Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    	
    	SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    	securityContext.setAuthentication(authentication);
    	SecurityContextHolder.setContext(securityContext);
    	
		UserType userType = UserType.EXTERNAL;
		AuthMethod authMethod = AuthMethod.GOOGLE;
        
        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setEmail("updateduser@example.com");
        updatedUser.setPassword("newpassword");
        updatedUser.setRoles("ADMIN");
        updatedUser.setUserType(userType);
        updatedUser.setAuthMethod(authMethod);

        User result = userService.update(updatedUser);

        assertNotNull(result);
        assertEquals(updatedUser.getEmail(), result.getEmail());
        assertEquals(user.getPassword(), result.getPassword()); // Password should remain unchanged
        assertEquals(user.getId(), result.getId());
        assertEquals("ADMIN", result.getRoles());
        
        assertEquals(updatedUser.getUserType(), result.getUserType());
        assertEquals(updatedUser.getAuthMethod(), result.getAuthMethod());
    }
    
    @Test
    public void testUpdate_WithUser() {
    	 // Create input values
    	Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    	
    	SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    	securityContext.setAuthentication(authentication);
    	SecurityContextHolder.setContext(securityContext);

        BigDecimal amount = BigDecimal.TEN.setScale(Constants.AMOUNT_SCALE);
        String notes = "this is a test transfer";
		String familyName = "familyName";
		String givenName = "givenName";
		String name = "name";
		String pictureUrl = "pictureUrl";
		boolean emailVerified = true;
		String oauthUserId = "oauthUserId";
		UserType userType = UserType.EXTERNAL;
        
        // Create MoneyTransferRequest instance using the static factory method
        MoneyTransferRequest request = MoneyTransferRequest.create("INTER_BANK",debitAccount.getId(), creditAccount.getId(), amount, debitAccount.getCurrency().toString(), creditAccount.getCurrency().toString(), BigDecimal.ONE, BigDecimal.TEN, notes, null, null);
        
        MoneyTransfer moneyTransfer = moneyTransferRequestMapper.toMoneyTransfer(user, request);
        moneyTransfer.setTransactionType(TransactionType.OWN_ACCOUNT);
        
        AccountStatement accountStatement = new AccountStatement(TransactionDirection.CREDIT, moneyTransfer, user, debitAccount); 

        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setEmail("updateduser@example.com");
        updatedUser.setPassword("newpassword");
        updatedUser.setRoles("ADMIN");
        updatedUser.setTransfers(new ArrayList<>(List.of(moneyTransfer)));
        updatedUser.setAccountStatements(new ArrayList<>(List.of(accountStatement)));
        updatedUser.setFamilyName(familyName);
        updatedUser.setGivenName(givenName);
        updatedUser.setName(name);
        updatedUser.setPictureUrl(pictureUrl);
        updatedUser.setEmailVerified(emailVerified);
        updatedUser.setExternalUserId(oauthUserId);
        updatedUser.setUserType(userType);

        User result = userService.update(updatedUser);

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());  // User can't update email
        assertEquals(user.getPassword(), result.getPassword()); // Password should remain unchanged
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getRoles(), result.getRoles()); // User can't update roles
        assertEquals(1, result.getTransfers().size());
        assertEquals(1, result.getAccountStatements().size());
        
        assertEquals(updatedUser.getFamilyName(), result.getFamilyName());
        assertEquals(updatedUser.getGivenName(), result.getGivenName());
        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(updatedUser.getPictureUrl(), result.getPictureUrl());
        assertEquals(updatedUser.isEmailVerified(), result.isEmailVerified());
        assertEquals(updatedUser.getExternalUserId(), result.getExternalUserId());
        assertEquals(updatedUser.getUserType(), result.getUserType());
    }
    
    @Test
    public void testUpdatePassword() {
    	 // Create input values
    	String newPassword = "123qwerty";
    	user.setPassword(newPassword);
    	user.setDefaultPassword(true);
    	user.setUserType(UserType.EXTERNAL);

        User result = userService.updatePassword(user);

        assertNotNull(result);
        assertTrue(passwordEncoder.matches(newPassword, result.getPassword())); // Password should change
        assertFalse(user.isDefaultPassword());
        assertEquals(UserType.LINKED, result.getUserType());
    }
    
    @Test
    public void testDisable() {
    	 // Create input values
    	user.setEnabled(false);
        User result = userService.update(user);

        assertNotNull(result);
        assertFalse(result.isEnabled()); // Password should change
    }
    
    @Test
    void testSoftDeleteUserById() {
    	// Create MoneyTransferRequest instance using the static factory method
        MoneyTransferRequest request = MoneyTransferRequest.create("OWN_ACCOUNT",debitAccount.getId(), creditAccount.getId(), BigDecimal.TEN, debitAccount.getCurrency().toString(), creditAccount.getCurrency().toString(), BigDecimal.ONE, BigDecimal.TEN, null, null, null);
        
        MoneyTransfer moneyTransfer = moneyTransferRequestMapper.toMoneyTransfer(user, request);
        moneyTransfer.setTransactionType(TransactionType.OWN_ACCOUNT);
        
        AccountStatement accountStatement = new AccountStatement(TransactionDirection.CREDIT, moneyTransfer, user, debitAccount); 

        user.setTransfers(new ArrayList<>(List.of(moneyTransfer)));
        user.setAccountStatements(new ArrayList<>(List.of(accountStatement)));
        
        // Call the service method
        user = userService.softDeleteUserById(user.getId());

        // Verify soft delete behavior
        assertTrue(user.isDeleted());
        user.getAccounts().forEach(account -> assertTrue(account.isDeleted()));
        user.getTransfers().forEach(transfer -> assertTrue(transfer.isDeleted()));
        user.getAccountStatements().forEach(statement -> assertTrue(statement.isDeleted()));
        
    }

    @Test
    public void testDeleteById() {
        userService.deleteById(user.getId());

        assertFalse(userRepository.findById(user.getId()).isPresent());
        
        assertThrows(ObjectNotFoundException.class, () -> {
        	userService.findById(user.getId());
        });
    }

    @Test
    void testLoadUserByUsername() {
        UserDetails userDetails = userService.loadUserByUsername("user@example.com");

        assertNotNull(userDetails);
        assertEquals(user.getEmail(), userDetails.getUsername());
    }
    
    @Test
    void testIsUserPresent() {
        User result = userService.isUserPresent(user.getEmail());
        assertNotNull(result);
        
        // Verify that collections are initialized
        assertTrue(Hibernate.isInitialized(result.getAccounts()));
        assertTrue(Hibernate.isInitialized(result.getTransfers()));
        assertTrue(Hibernate.isInitialized(result.getAccountStatements()));
        
        assertNull(userService.isUserPresent("i.dont@exist.com"));
    }
    
    @Test
    void testFindUserByAccountId() {
    	user.setAccounts(accountService.create(user));
        User result = userService.findUserByAccountId(user.getAccounts().get(0).getId());
        assertNotNull(result);
        
//        Verify that collections are initialized
        assertTrue(Hibernate.isInitialized(result.getAccounts()));
        assertTrue(Hibernate.isInitialized(result.getTransfers()));
        assertTrue(Hibernate.isInitialized(result.getAccountStatements()));
        
        assertNull(userService.findUserByAccountId(0L));
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistent@example.com");
        });
    }

    @Test
    void testUpdateLastLoginDate() {
        userService.updateLastLoginDate("user@example.com");

        Optional<User> updatedUser = userService.findByEmail("user@example.com");
        assertTrue(updatedUser.isPresent());
        assertNotNull(updatedUser.get().getLastLoginDate());
    }
    
    @Test
    void testIsUser() {
    	// Arrange
    	Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    	
    	SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    	securityContext.setAuthentication(authentication);
    	SecurityContextHolder.setContext(securityContext);
    	
        // Act
        boolean isUser = userService.isUser(authentication);
        
        // Assert
        assertTrue(isUser);
    }
    
    @Test
    void testIsAdmin() {
    	// Arrange
    	Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    	
    	SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    	securityContext.setAuthentication(authentication);
    	SecurityContextHolder.setContext(securityContext);
    	
        // Act
        boolean isAdmin = userService.isAdmin(authentication);
        
        // Assert
        assertTrue(isAdmin);
    }
}
