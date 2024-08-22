package com.fastcash.moneytransfer.validation;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    private final ReloadableResourceBundleMessageSource messageSource;

    public UserValidator(ReloadableResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public UsernameNotFoundException handleUserNotFound() {
        return new UsernameNotFoundException(
            messageSource.getMessage("username.password.incorrect", null, LocaleContextHolder.getLocale())
        );
    }
}
