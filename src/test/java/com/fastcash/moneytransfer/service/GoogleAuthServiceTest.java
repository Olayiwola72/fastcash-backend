package com.fastcash.moneytransfer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.fastcash.moneytransfer.dto.GoogleUserResponse;
import com.fastcash.moneytransfer.dto.UserRequestMapper;
import com.fastcash.moneytransfer.exception.InvalidIDTokenException;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

class GoogleAuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReloadableResourceBundleMessageSource messageSource;

    @Mock
    private UserRequestMapper userRequestMapper;

    @Mock
    private GoogleIdTokenVerifier verifier;

    @InjectMocks
    private GoogleAuthService googleAuthService;

    private final String clientId = "test-client-id";
    private final String email = "test@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        googleAuthService = new GoogleAuthService(clientId, userService, userRepository, messageSource, userRequestMapper);
        googleAuthService.verifier = verifier;
    }

    @Test
    void testGetUser_Update_WithValidToken() throws GeneralSecurityException, IOException {
        String idTokenString = "valid-id-token";

        GoogleIdToken idToken = mock(GoogleIdToken.class);
        Payload payload = mock(Payload.class);

        when(verifier.verify(idTokenString)).thenReturn(idToken);
        when(idToken.getPayload()).thenReturn(payload);

        when(payload.getEmail()).thenReturn(email);

        User existingUser = new User();
        when(userService.isUserPresent(email)).thenReturn(existingUser);

        User mappedUser = new User();
        when(userRequestMapper.toGoogleUser(existingUser, payload)).thenReturn(mappedUser);

        User savedUser = new User();
        when(userRepository.save(mappedUser)).thenReturn(savedUser);

        GoogleUserResponse result = googleAuthService.getUser(idTokenString);

        assertNotNull(result.googleUser());
        assertEquals(savedUser, result.googleUser());
        assertEquals(existingUser, result.existingUser());

        verify(verifier, times(1)).verify(idTokenString);
        verify(userService, times(1)).isUserPresent(email);
        verify(userRequestMapper, times(1)).toGoogleUser(existingUser, payload);
        verify(userRepository, times(1)).save(mappedUser);
    }
    
    @Test
    void testGetUser_Create_WithValidToken() throws GeneralSecurityException, IOException {
        String idTokenString = "valid-id-token";

        GoogleIdToken idToken = mock(GoogleIdToken.class);
        Payload payload = mock(Payload.class);

        when(verifier.verify(idTokenString)).thenReturn(idToken);
        when(idToken.getPayload()).thenReturn(payload);

        when(payload.getEmail()).thenReturn(email);

        User existingUser = null;
        when(userService.isUserPresent(email)).thenReturn(existingUser);

        User mappedUser = new User();
        when(userRequestMapper.toGoogleUser(existingUser, payload)).thenReturn(mappedUser);

        User savedUser = new User();
        when(userService.create(mappedUser)).thenReturn(savedUser);

        GoogleUserResponse result = googleAuthService.getUser(idTokenString);

        assertNotNull(result.googleUser());
        assertEquals(savedUser, result.googleUser());
        assertEquals(existingUser, result.existingUser());

        verify(verifier, times(1)).verify(idTokenString);
        verify(userService, times(1)).isUserPresent(email);
        verify(userRequestMapper, times(1)).toGoogleUser(existingUser, payload);
        verify(userService, times(1)).create(mappedUser);
    }

    @Test
    void testGetUser_WithInvalidToken() throws GeneralSecurityException, IOException {
        String idTokenString = "invalid-id-token";

        when(verifier.verify(idTokenString)).thenReturn(null);

        when(messageSource.getMessage(eq("TokenInvalid"), any(), eq(LocaleContextHolder.getLocale())))
            .thenReturn("Token is invalid");

        InvalidIDTokenException exception = assertThrows(InvalidIDTokenException.class, () -> {
            googleAuthService.getUser(idTokenString);
        });

        assertEquals("Token is invalid", exception.getMessage());

        verify(verifier, times(1)).verify(idTokenString);
        verify(userService, never()).isUserPresent(any());
        verify(userRequestMapper, never()).toGoogleUser(any(), any());
        verify(userService, never()).create(any(User.class));
    }
    
}
