package com.fastcash.moneytransfer.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

class AccountUpdateAuthorizationManagerTest {

    private UserService userService;
    private AccountUpdateAuthorizationManager authorizationManager;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        authorizationManager = new AccountUpdateAuthorizationManager(userService);
    }

    @Test
    void testCheck_AuthorizedAsAdmin() {
        // Arrange
        String accountId = "123";
        String email = "admin@example.com";
        User user = new User();
        user.setEmail(email);
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/account/" + accountId);
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        
        when(userService.findByEmail(email)).thenReturn(Optional.of(user));
        when(userService.isAdmin(authentication)).thenReturn(true);

        Supplier<Authentication> authenticationSupplier = () -> authentication;
        RequestAuthorizationContext context = new RequestAuthorizationContext(request);

        // Act
        AuthorizationDecision decision = authorizationManager.check(authenticationSupplier, context);

        // Assert
        assertTrue(decision.isGranted());
    }

    @Test
    void testCheck_AuthorizedAsUser() {
        // Arrange
        String accountId = "123";
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);
        UserAccount account = new UserAccount();
        account.setId(Long.parseLong(accountId));
        user.setAccounts(List.of(account));
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/account/" + accountId);
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        
        when(userService.findByEmail(email)).thenReturn(Optional.of(user));
        when(userService.isUser(authentication)).thenReturn(true);

        Supplier<Authentication> authenticationSupplier = () -> authentication;
        RequestAuthorizationContext context = new RequestAuthorizationContext(request);

        // Act
        AuthorizationDecision decision = authorizationManager.check(authenticationSupplier, context);

        // Assert
        assertTrue(decision.isGranted());
    }

    @Test
    void testCheck_NotAuthorized() {
        // Arrange
        String accountId = "123";
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/account/" + accountId);
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        
        when(userService.findByEmail(email)).thenReturn(Optional.of(user));
        when(userService.isUser(authentication)).thenReturn(true);

        Supplier<Authentication> authenticationSupplier = () -> authentication;
        RequestAuthorizationContext context = new RequestAuthorizationContext(request);

        // Act
        AuthorizationDecision decision = authorizationManager.check(authenticationSupplier, context);

        // Assert
        assertFalse(decision.isGranted());
    }
}

