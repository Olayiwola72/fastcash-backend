package com.fastcash.moneytransfer.security;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserDetailsChecker implements UserDetailsChecker {
	
	private final ReloadableResourceBundleMessageSource messageSource;
	
	public DefaultUserDetailsChecker(ReloadableResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

    @Override
    public void check(UserDetails userDetails) {
    	
        if (!userDetails.isEnabled()) {
            throw new DisabledException(messageSource.getMessage("DisabledException", null, LocaleContextHolder.getLocale()));
        }

        if (!userDetails.isAccountNonLocked()) {
            throw new LockedException(messageSource.getMessage("LockedException", null, LocaleContextHolder.getLocale()));
        }

        if (!userDetails.isAccountNonExpired()) {
            throw new AccountExpiredException(messageSource.getMessage("AccountExpiredException", null, LocaleContextHolder.getLocale()));
        }

        if (!userDetails.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException(messageSource.getMessage("CredentialsExpiredException", null, LocaleContextHolder.getLocale()));
        }
        
    }
    
}
