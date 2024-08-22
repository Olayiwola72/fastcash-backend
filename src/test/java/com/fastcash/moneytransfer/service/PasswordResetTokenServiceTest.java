package com.fastcash.moneytransfer.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.dto.PasswordUpdateRequest;
import com.fastcash.moneytransfer.dto.UserRequestMapper;
import com.fastcash.moneytransfer.model.NotificationContext;
import com.fastcash.moneytransfer.model.PasswordResetToken;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.repository.PasswordResetTokenRepository;
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;

class PasswordResetTokenServiceTest {
	
	@InjectMocks
	private PasswordResetTokenService passwordResetTokenService;
	
	@Mock
	private PasswordResetTokenRepository tokenRepository;
	
	@Mock
	private EmailNotificationService emailNotificationService;
	
	@Mock
	private UserService userService;
	
	@Mock
	private UserRequestMapper userRequestMapper;
	
	@Mock
	private ReloadableResourceBundleMessageSource messageSource;
	
	private User user;
	
	private final String email = "reset@email.com";
	private final String token = "token";
	private final String newPassword = "newPassword";
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		
		user = new User();
	}
	
	@Test
	void testCreatePasswordResetToken() {
		when(userService.findByEmail(email)).thenReturn(Optional.of(user));
		
		String token = passwordResetTokenService.createPasswordResetToken(email);
		
		verify(tokenRepository).save(any(PasswordResetToken.class));
		verify(emailNotificationService).sendUserPasswordResetNotification(any(NotificationContext.class));
		assertNotNull(token);
	}
	
	@Test
	void testResetPassword_TokenInvalid() {
		when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());
		
		assertThrows(IllegalArgumentException.class, () -> 
	        passwordResetTokenService.resetPassword(token, email)
	    );
	}
	
	@Test
	void testResetPassword_TokenExpired() {
		PasswordResetToken resetToken = new PasswordResetToken(token, user, LocalDateTime.now().minusHours(Constants.PASSWORD_RESET_TOKEN_EXPIRY_HOURS));
		when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
		
		assertThrows(IllegalArgumentException.class, () -> 
	        passwordResetTokenService.resetPassword(token, email)
	    );
	}
	
	@Test
	void testResetPassword_Success() {
	    // Mock the reset token
	    PasswordResetToken resetToken = new PasswordResetToken(token, user, LocalDateTime.now().plusHours(Constants.PASSWORD_RESET_TOKEN_EXPIRY_HOURS));
	    when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
	    
	    // Mock the userRequestMapper to return an updated user object
	    User updatedUser = new User();
	    when(userRequestMapper.toUpdateUserPassword(any(User.class), any(PasswordUpdateRequest.class))).thenReturn(updatedUser);

	    // Call the resetPassword method
	    passwordResetTokenService.resetPassword(token, newPassword);

	    // Verify interactions
	    verify(userRequestMapper).toUpdateUserPassword(eq(resetToken.getUser()), eq(new PasswordUpdateRequest(newPassword)));
	    verify(userService).updatePassword(eq(updatedUser));
	    verify(tokenRepository).delete(resetToken);
	}

}
