package com.fastcash.moneytransfer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.fastcash.moneytransfer.exception.TokenRefreshException;
import com.fastcash.moneytransfer.model.RefreshToken;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.repository.RefreshTokenRepository;

class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private ReloadableResourceBundleMessageSource messageSource;

    private RefreshTokenService refreshTokenService;

    private User user;
    private RefreshToken refreshToken;
    private String token;
    private final String userAgent = "userAgent";
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        token = UUID.randomUUID().toString();
        user = new User();
        refreshToken = new RefreshToken(token, LocalDateTime.now().plusMinutes(60), user, userAgent);
        
        refreshTokenService = new RefreshTokenService(60L, refreshTokenRepository, messageSource);
    }

    @Test
    void testFindByToken_TokenExists() {
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));
        
        RefreshToken foundToken = refreshTokenService.findByToken(token);
        
        assertNotNull(foundToken);
        assertEquals(token, foundToken.getToken());
        verify(refreshTokenRepository, times(1)).findByToken(token);
    }

    @Test
    void testFindByToken_TokenDoesNotExist() {
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());
        when(messageSource.getMessage("TokenInvalid", null, LocaleContextHolder.getLocale())).thenReturn("Token is invalid");

        TokenRefreshException exception = assertThrows(TokenRefreshException.class, () -> {
            refreshTokenService.findByToken(token);
        });

        assertNotNull(exception.getMessage());
        verify(refreshTokenRepository, times(1)).findByToken(token);
    }

    @Test
    void testCreateRefreshToken() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        RefreshToken createdToken = refreshTokenService.createRefreshToken(user, userAgent);

        assertNotNull(createdToken);
        assertEquals(user, createdToken.getUser());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void testFindByUser_RefreshTokenExists() {
        when(refreshTokenRepository.findByUserAndUserAgent(user, userAgent)).thenReturn(Optional.of(refreshToken));

        RefreshToken foundToken = refreshTokenService.findByUserAndUserAgent(user, userAgent);

        assertNotNull(foundToken);
        assertEquals(refreshToken, foundToken);
        verify(refreshTokenRepository, times(1)).findByUserAndUserAgent(user, userAgent);
    }

    @Test
    void testFindByUser_RefreshTokenDoesNotExist() {
        when(refreshTokenRepository.findByUserAndUserAgent(user, userAgent)).thenReturn(Optional.empty());
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        RefreshToken createdToken = refreshTokenService.findByUserAndUserAgent(user, userAgent);

        assertNotNull(createdToken);
        assertEquals(user, createdToken.getUser());
        verify(refreshTokenRepository, times(1)).findByUserAndUserAgent(user, userAgent);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void testVerifyExpiration_TokenNotExpired() {
        RefreshToken validToken = new RefreshToken(token, LocalDateTime.now().plusMinutes(10), user, userAgent);

        RefreshToken resultToken = refreshTokenService.verifyExpiration(validToken);

        assertNotNull(resultToken);
        assertEquals(validToken, resultToken);
        verify(refreshTokenRepository, never()).delete(validToken);
    }

    @Test
    void testVerifyExpiration_TokenExpired() {
        RefreshToken expiredToken = new RefreshToken(token, LocalDateTime.now().minusMinutes(10), user, userAgent);
        when(messageSource.getMessage("TokenExpired", null, LocaleContextHolder.getLocale())).thenReturn("Token has expired");

        TokenRefreshException exception = assertThrows(TokenRefreshException.class, () -> {
            refreshTokenService.verifyExpiration(expiredToken);
        });

        assertNotNull(exception.getMessage());
        verify(refreshTokenRepository, times(1)).delete(expiredToken);
    }

    @Test
    void testDeleteByUser() {
        doNothing().when(refreshTokenRepository).deleteByUserAndUserAgent(user, userAgent);

        refreshTokenService.deleteByUserAndUserAgent(user, userAgent);

        verify(refreshTokenRepository, times(1)).deleteByUserAndUserAgent(user, userAgent);
    }
}
