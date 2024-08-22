package com.fastcash.moneytransfer.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastcash.moneytransfer.exception.TokenRefreshException;
import com.fastcash.moneytransfer.model.RefreshToken;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.repository.RefreshTokenRepository;

@Service
@Transactional
public class RefreshTokenService {
    
    private final Duration refreshTokenDuration;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ReloadableResourceBundleMessageSource messageSource;

    public RefreshTokenService(
        @Value("${jwt.refreshExpirationMs}") Long refreshTokenDurationMs,
        RefreshTokenRepository refreshTokenRepository,
        ReloadableResourceBundleMessageSource messageSource
    ) {
        this.refreshTokenDuration = Duration.ofMillis(refreshTokenDurationMs);
        this.refreshTokenRepository = refreshTokenRepository;
        this.messageSource = messageSource;
    }

    public RefreshToken findByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
            .orElseThrow(() -> new TokenRefreshException(
                null, 
                messageSource.getMessage("TokenInvalid", null, LocaleContextHolder.getLocale())
            ));
        
        return refreshToken;
    }
    
    public RefreshToken createRefreshToken(User user, String userAgent) {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(token, LocalDateTime.now().plus(refreshTokenDuration), user, userAgent);
        return refreshTokenRepository.save(refreshToken);
    }
    
    public RefreshToken findByUserAndUserAgent(User user, String userAgent) {
        return refreshTokenRepository.findByUserAndUserAgent(user, userAgent)
            .orElseGet(() -> createRefreshToken(user, userAgent)); 
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            
            throw new TokenRefreshException(
                token.getToken(), 
                messageSource.getMessage("TokenExpired", null, LocaleContextHolder.getLocale())
            );
        }

        return token;
    }

    public void deleteByUserAndUserAgent(User user, String userAgent) {
        refreshTokenRepository.deleteByUserAndUserAgent(user, userAgent);
    }
}
