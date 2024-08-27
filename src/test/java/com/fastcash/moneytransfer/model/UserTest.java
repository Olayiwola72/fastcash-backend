package com.fastcash.moneytransfer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import com.fastcash.moneytransfer.enums.AuthMethod;
import com.fastcash.moneytransfer.enums.UserRoles;
import com.fastcash.moneytransfer.enums.UserType;

class UserTest {
	
	@Test
    void testDefaultConstructor() {
        // Arrange
        // Act
        User user = new User();

        // Assert
        assertNull(user.getId()); // Id should not be null
        assertTrue(user.isEnabled()); // User should be enabled
        assertTrue(user.getAccounts().isEmpty()); // Accounts should be an empty list
        assertTrue(user.getTransfers().isEmpty()); // Transfer should be an empty list
        assertTrue(user.getAccountStatements().isEmpty()); // UserAccount Statement should be an empty list
        assertEquals(LocaleContextHolder.getLocale(), user.getPreferredLanguage()); // preferredLanguage should not be null
        assertNotNull(user.getCreatedAt()); // CreatedAt should not be null
        assertFalse(user.isDeleted()); // deleted should be false initially
        assertNull(user.getLastLoginDate());
        assertFalse(user.isDefaultPassword());
    }
	
	@Test
    void testUserInitialization_Internal() {
        // Arrange
		String email = "user@email.com";
		String password = "123456";
		AuthMethod authMethod = AuthMethod.LOCAL;

        // Act
        User user = new User(email, password);

        // Assert
        assertNull(user.getId()); // Id should not be null
        assertTrue(user.isEnabled()); // User should be enabled
        assertTrue(user.getTransfers().isEmpty()); // Transfer should be an empty list
        assertNotNull(user.getCreatedAt()); // CreatedAt should not be null
        assertFalse(user.isDeleted()); // deleted should be false initially
        assertEquals(authMethod, user.getAuthMethod());
        
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(UserRoles.USER.toString(), user.getRoles());
        assertNull(user.getLastLoginDate());
        assertEquals(UserType.INTERNAL, user.getUserType());
        assertFalse(user.isDefaultPassword());
    }
	
	@Test
    void testUserInitialization_External() {
        // Arrange
		String email = "user@email.com";
		String password = "123456";
		String familyName = "familyName";
		String givenName = "givenName";
		String name = "name";
		String pictureUrl = "pictureUrl";
		boolean emailVerified = true;
		String oauthUserId = "oauthUserId";

        // Act
        User user = new User(email, password, familyName, givenName, name, pictureUrl, emailVerified, oauthUserId);

        // Assert
        assertEquals(familyName, user.getFamilyName());
        assertEquals(givenName, user.getGivenName());
        assertEquals(name, user.getName());
        assertEquals(pictureUrl, user.getPictureUrl());
        assertEquals(emailVerified, user.isEmailVerified());
        assertEquals(oauthUserId, user.getExternalUserId());
        assertEquals(UserType.EXTERNAL, user.getUserType());
        assertTrue(user.isDefaultPassword());
    }
	
	@Test
    public void testGettersAndSetters() {
		// Arrange
		Long userId = 123L;
		String email = "user@email.com";
		String password = "123456";
		AuthMethod authMethod = AuthMethod.LOCAL;
		String role = UserRoles.USER.toString();
		UserAccount userAccount = new UserAccount();
		MoneyTransfer transfer = new MoneyTransfer();
		List<UserAccount> userAccounts = List.of(userAccount);
		List<MoneyTransfer> transfers = List.of(transfer);
		List<AccountStatement> accountStatements = List.of(new AccountStatement());
		String familyName = "familyName";
		String givenName = "givenName";
		String name = "name";
		String pictureUrl = "pictureUrl";
		boolean emailVerified = true;
		String oauthUserId = "oauthUserId";
		LocalDateTime createdAt = LocalDateTime.now();
        Date lastLoginDate = new Date();
        UserType userType = UserType.INTERNAL;
        int version = 1;

        // Act
        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setPassword(password);
        user.setEnabled(false);
        user.setRoles(role);
        user.setAuthMethod(authMethod);
        user.setAccounts(userAccounts);
        user.setTransfers(transfers);
        user.setAccountStatements(accountStatements);
        user.setCreatedAt(createdAt);
        user.setDeleted(true);
        user.setLastLoginDate(lastLoginDate);
        user.setVersion(version);
        user.setFamilyName(familyName);
        user.setGivenName(givenName);
        user.setName(name);
        user.setPictureUrl(pictureUrl);
        user.setEmailVerified(emailVerified);
        user.setDefaultPassword(true);
        user.setExternalUserId(oauthUserId);
        user.setUserType(userType);

        // Assert
        assertEquals(userId, user.getId());    
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertFalse(user.isEnabled()); 
        assertEquals(role, user.getRoles());
        assertEquals(authMethod, user.getAuthMethod());   
        assertEquals(1, user.getAccounts().size()); 
        assertEquals(userAccounts.get(0), user.getAccounts().get(0)); 
        assertEquals(1, user.getTransfers().size()); 
        assertEquals(transfers.get(0), user.getTransfers().get(0));
        assertEquals(1, user.getAccountStatements().size()); 
        assertEquals(accountStatements.get(0), user.getAccountStatements().get(0));
        assertEquals(createdAt, user.getCreatedAt());
        assertTrue(user.isDefaultPassword());
        assertEquals(lastLoginDate, user.getLastLoginDate());
        assertEquals(version, user.getVersion());
        assertEquals(familyName, user.getFamilyName());
        assertEquals(givenName, user.getGivenName());
        assertEquals(name, user.getName());
        assertEquals(pictureUrl, user.getPictureUrl());
        assertEquals(emailVerified, user.isEmailVerified());
        assertTrue(user.isDefaultPassword());
        assertEquals(oauthUserId, user.getExternalUserId());
        assertEquals(userType, user.getUserType());
    }
	
}
