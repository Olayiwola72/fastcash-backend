package com.fastcash.moneytransfer.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.fastcash.moneytransfer.service.UserService;

@Component
public class TokenAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final DefaultUserDetailsChecker userDetailsChecker;


    public TokenAuthenticationProvider(UserService userService, DefaultUserDetailsChecker userDetailsChecker) {
        this.userService = userService;
        this.userDetailsChecker = userDetailsChecker;  // Use the custom checker
    }

    public Authentication authenticateUser(String username) throws AuthenticationException {
        UserDetails userDetails = userService.loadUserByUsername(username);
        
        // Perform default checks using custom checker
        userDetailsChecker.check(userDetails);
        
        // Return the authenticated token
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return authenticateUser(authentication.getName());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
