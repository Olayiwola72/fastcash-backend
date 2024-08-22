package com.fastcash.moneytransfer.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {
	
	@Mock
    private ReloadableResourceBundleMessageSource messageSource;

    @InjectMocks
    private UserValidator userValidator;
	
	@Test
	void testHandleUserNotFound() {
		// Arrange
        String errorMessage = "User not found";
        Locale locale = LocaleContextHolder.getLocale();
        when(messageSource.getMessage("username.password.incorrect", null, locale)).thenReturn(errorMessage);

        // Act
        UsernameNotFoundException exception = userValidator.handleUserNotFound();

        // Assert
        assertEquals(errorMessage, exception.getMessage());
	}
}
