package com.fastcash.moneytransfer.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;

public class DefaultUserDetailsCheckerTest {

    private DefaultUserDetailsChecker checker;
    private ReloadableResourceBundleMessageSource messageSource;
    
    @BeforeEach
    public void setUp() {
        messageSource = mock(ReloadableResourceBundleMessageSource.class);
        checker = new DefaultUserDetailsChecker(messageSource);
    }

    @Test
    public void testCheckDisabledUser() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.isEnabled()).thenReturn(false);
        when(messageSource.getMessage("DisabledException", null, LocaleContextHolder.getLocale()))
            .thenReturn("User is disabled");

        DisabledException thrown = assertThrows(DisabledException.class, () -> checker.check(userDetails));
        assertEquals("User is disabled", thrown.getMessage());
    }

    @Test
    public void testCheckLockedUser() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.isEnabled()).thenReturn(true);
        when(userDetails.isAccountNonLocked()).thenReturn(false);
        when(messageSource.getMessage("LockedException", null, LocaleContextHolder.getLocale()))
            .thenReturn("User account is locked");

        LockedException thrown = assertThrows(LockedException.class, () -> checker.check(userDetails));
        assertEquals("User account is locked", thrown.getMessage());
    }

    @Test
    public void testCheckExpiredAccount() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.isEnabled()).thenReturn(true);
        when(userDetails.isAccountNonLocked()).thenReturn(true);
        when(userDetails.isAccountNonExpired()).thenReturn(false);
        when(messageSource.getMessage("AccountExpiredException", null, LocaleContextHolder.getLocale()))
            .thenReturn("User account has expired");

        AccountExpiredException thrown = assertThrows(AccountExpiredException.class, () -> checker.check(userDetails));
        assertEquals("User account has expired", thrown.getMessage());
    }

    @Test
    public void testCheckExpiredCredentials() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.isEnabled()).thenReturn(true);
        when(userDetails.isAccountNonLocked()).thenReturn(true);
        when(userDetails.isAccountNonExpired()).thenReturn(true);
        when(userDetails.isCredentialsNonExpired()).thenReturn(false);
        when(messageSource.getMessage("CredentialsExpiredException", null, LocaleContextHolder.getLocale()))
            .thenReturn("User credentials have expired");

        CredentialsExpiredException thrown = assertThrows(CredentialsExpiredException.class, () -> checker.check(userDetails));
        assertEquals("User credentials have expired", thrown.getMessage());
    }
}
