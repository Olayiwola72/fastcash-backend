package com.fastcash.moneytransfer.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.fastcash.moneytransfer.service.UserService;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private final UserService userService;

    public AuthenticationSuccessListener(UserService userService) {
        this.userService = userService;
    }
    
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String userName = null;
        Authentication authentication = event.getAuthentication();

        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwtToken = (Jwt) authentication.getPrincipal();
            userName = jwtToken.getSubject();
        } else {
            userName = ((UserDetails) authentication.getPrincipal()).getUsername();
        }

        userService.updateLastLoginDate(userName);
    }
    
}