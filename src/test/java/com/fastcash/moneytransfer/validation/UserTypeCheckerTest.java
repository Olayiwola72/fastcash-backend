package com.fastcash.moneytransfer.validation;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.fastcash.moneytransfer.enums.AuthMethod;
import com.fastcash.moneytransfer.enums.UserType;
import com.fastcash.moneytransfer.exception.UserTypeCheckerException;
import com.fastcash.moneytransfer.model.User;

class UserTypeCheckerTest {
	
	@Mock
	private ReloadableResourceBundleMessageSource messageSource;
	
	@InjectMocks
	private UserTypeChecker userTypeChecker;
	
	private User user;
	
	@BeforeEach
    void setUp() {
    	MockitoAnnotations.openMocks(this);
    	userTypeChecker = new UserTypeChecker(messageSource);
    	
    	user = new User();
    	user.setUserType(UserType.EXTERNAL);
    	user.setAuthMethod(AuthMethod.GOOGLE);
    }
	
	@Test
	void testHandleUserNotInternal() {
		UserTypeCheckerException userTypeCheckerException = userTypeChecker.handleUserNotInternal(user);
		assertNotNull(userTypeCheckerException);
	}
}
