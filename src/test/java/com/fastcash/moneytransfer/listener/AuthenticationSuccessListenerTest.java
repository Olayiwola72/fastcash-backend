package com.fastcash.moneytransfer.listener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.fastcash.moneytransfer.service.UserService;

@ExtendWith(MockitoExtension.class)
public class AuthenticationSuccessListenerTest {
	
	@Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationSuccessListener authenticationSuccessListener;
    
    
	@Test
	void testOnApplicationEventWithJwtAuthentication() {
    	String userName = "testUser";

    	// Mock Jwt token
    	Jwt jwtToken = mock(Jwt.class);
    	when(jwtToken.getSubject()).thenReturn(userName);

    	// Mock Authentication object with Jwt token
    	JwtAuthenticationToken authentication = mock(JwtAuthenticationToken.class);
    	when(authentication.getPrincipal()).thenReturn(jwtToken);

    	// Set the Authentication object to SecurityContextHolder
    	SecurityContextHolder.getContext().setAuthentication(authentication);

    	// Create AuthenticationSuccessEvent
    	AuthenticationSuccessEvent event = new AuthenticationSuccessEvent(authentication);

    	// Trigger onApplicationEvent
    	authenticationSuccessListener.onApplicationEvent(event);

    	// Assert
    	verify(userService, times(1)).updateLastLoginDate(userName);
	}
	
    @Test
    void testOnApplicationEventWithUsernamePasswordAuthentication() {
    	String userName = "testUser";

        // Mock Authentication object with username password authentication
        Authentication authentication = mock(UsernamePasswordAuthenticationToken.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(userName);

        // Set the Authentication object to SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Create AuthenticationSuccessEvent
        AuthenticationSuccessEvent event = new AuthenticationSuccessEvent(authentication);

        // Trigger onApplicationEvent
        authenticationSuccessListener.onApplicationEvent(event);

        // Assert
        verify(userService, times(1)).updateLastLoginDate(userName);
    }
}
