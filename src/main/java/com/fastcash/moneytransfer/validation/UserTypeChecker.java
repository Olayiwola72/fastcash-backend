package com.fastcash.moneytransfer.validation;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import com.fastcash.moneytransfer.exception.UserTypeCheckerException;
import com.fastcash.moneytransfer.model.User;

@Component
public class UserTypeChecker {
	
	private final ReloadableResourceBundleMessageSource messageSource;
	
	public UserTypeChecker(ReloadableResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public UserTypeCheckerException handleUserNotInternal(User user) {
		String authProvider = user.getAuthMethod().toString();
		
		return new UserTypeCheckerException(
			messageSource.getMessage(
            	"UserTypeNotInternal", 
            	new Object[] {
            		authProvider.substring(0, 1) + authProvider.substring(1, authProvider.length()).toLowerCase()	
            	},
            	LocaleContextHolder.getLocale()
            )
		);
	}
}
