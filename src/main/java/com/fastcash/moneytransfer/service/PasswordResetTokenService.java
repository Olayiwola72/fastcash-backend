package com.fastcash.moneytransfer.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.dto.PasswordUpdateRequest;
import com.fastcash.moneytransfer.dto.UserRequestMapper;
import com.fastcash.moneytransfer.enums.NotificationType;
import com.fastcash.moneytransfer.model.NotificationContext;
import com.fastcash.moneytransfer.model.PasswordResetToken;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.repository.PasswordResetTokenRepository;

@Service
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserService userService;
    private final ReloadableResourceBundleMessageSource messageSource;
    private final EmailNotifiable emailNotifiable;
    private final UserRequestMapper userRequestMapper;
    
    public PasswordResetTokenService(
    	PasswordResetTokenRepository tokenRepository, 
    	UserService userService,
    	ReloadableResourceBundleMessageSource messageSource,
    	EmailNotifiable emailNotifiable,
    	UserRequestMapper userRequestMapper
    ) {
    	this.tokenRepository = tokenRepository;
    	this.userService = userService;
    	this.messageSource = messageSource;
    	this.emailNotifiable = emailNotifiable;
    	this.userRequestMapper = userRequestMapper;
    }

    public String createPasswordResetToken(String email) {
        User user = userService.findByEmail(email).get();

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user, LocalDateTime.now().plusHours(Constants.PASSWORD_RESET_TOKEN_EXPIRY_HOURS));
        
        tokenRepository.save(resetToken);  
        
        emailNotifiable.sendUserPasswordResetNotification(new NotificationContext(NotificationType.EMAIL,user, token));
        
        return token;
    }
    
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException(
                	messageSource.getMessage("TokenInvalid", null, LocaleContextHolder.getLocale())
                ));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
            	messageSource.getMessage("TokenExpired", null, LocaleContextHolder.getLocale())
            );
        }
        
        User user = resetToken.getUser();
        user = userRequestMapper.toUpdateUserPassword(resetToken.getUser(), new PasswordUpdateRequest(newPassword));
        userService.updatePassword(user);
        
        tokenRepository.delete(resetToken);
    }
    
}