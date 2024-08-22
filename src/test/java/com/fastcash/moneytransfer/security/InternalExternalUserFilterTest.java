package com.fastcash.moneytransfer.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.fastcash.moneytransfer.enums.UserType;
import com.fastcash.moneytransfer.exception.UserTypeCheckerException;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.repository.UserRepository;
import com.fastcash.moneytransfer.validation.UserTypeChecker;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class InternalExternalUserFilterTest {
	
	@Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HandlerExceptionResolver resolver;
    
    @Mock
    private ReloadableResourceBundleMessageSource messageSource;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserTypeChecker userTypeChecker;

    @InjectMocks
    private InternalExternalUserFilter filter;
    

    @BeforeEach
    void setUp() {
    	MockitoAnnotations.openMocks(this);
        filter = new InternalExternalUserFilter(authenticationManager, resolver, messageSource, userRepository, userTypeChecker);
    }
    
    @Test
    void doFilterInternal_WithValidCredentials_ShouldNotResolveException() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic " + java.util.Base64.getEncoder().encodeToString("username:password".getBytes()));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(resolver, never()).resolveException(any(HttpServletRequest.class), any(HttpServletResponse.class), isNull(), any(AuthenticationException.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithEmptyUsername_ShouldResolveException() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic " + java.util.Base64.getEncoder().encodeToString(":password".getBytes()));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(resolver, times(1)).resolveException(any(HttpServletRequest.class), any(HttpServletResponse.class), isNull(), any(AuthenticationException.class));
    }

    @Test
    void doFilterInternal_WithEmptyPassword_ShouldResolveException() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic " + java.util.Base64.getEncoder().encodeToString("username:".getBytes()));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(resolver, times(1)).resolveException(any(HttpServletRequest.class), any(HttpServletResponse.class), isNull(), any(AuthenticationException.class));
    }

    @Test
    void doFilterInternal_WithEmptyCredentials_ShouldResolveException() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic " + java.util.Base64.getEncoder().encodeToString(":".getBytes()));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(resolver, times(1)).resolveException(any(HttpServletRequest.class), any(HttpServletResponse.class), isNull(), any(AuthenticationException.class));
    }


    @Test
    void doFilterInternal_WithInvalidCredentialsFormat_ShouldResolveException() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic " + java.util.Base64.getEncoder().encodeToString("invalid_format".getBytes()));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(resolver, times(1)).resolveException(any(HttpServletRequest.class), any(HttpServletResponse.class), isNull(), any(AuthenticationException.class));
    }
    
    
    @Test
    void testDoFilterInternal_UserNotInternal() throws Exception {
        User user = new User();
        user.setUserType(UserType.EXTERNAL);

        when(request.getHeader("Authorization")).thenReturn("Basic " + java.util.Base64.getEncoder().encodeToString("username:password".getBytes()));
        when(userRepository.findByEmailAndDeletedIsFalse("username")).thenReturn(Optional.of(user));
        when(userTypeChecker.handleUserNotInternal(user)).thenReturn(new UserTypeCheckerException("User is not internal"));
        
        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(resolver, times(1)).resolveException(any(HttpServletRequest.class), any(HttpServletResponse.class), isNull(), any(AuthenticationException.class));
    }
    
    @Test
    void testDoFilterInternal_UserInternal() throws Exception {
        User user = new User();
        user.setUserType(UserType.INTERNAL);

        when(request.getHeader("Authorization")).thenReturn("Basic " + java.util.Base64.getEncoder().encodeToString("username:password".getBytes()));
        when(userRepository.findByEmailAndDeletedIsFalse("username")).thenReturn(Optional.of(user));
        
        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(resolver, never()).resolveException(any(), any(), any(), any());
        verify(filterChain).doFilter(any(), any());
    }

}
