package com.fastcash.moneytransfer.validation;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.fastcash.moneytransfer.annotation.ValidAdmin;
import com.fastcash.moneytransfer.config.MessageSourceConfig;
import com.fastcash.moneytransfer.enums.UserType;
import com.fastcash.moneytransfer.model.Admin;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidAdminValidator implements ConstraintValidator<ValidAdmin, Admin> {
	
private ReloadableResourceBundleMessageSource messageSource;
	
	public ValidAdminValidator() {
		messageSource = new MessageSourceConfig().messageSource();
	}

	@Override
    public boolean isValid(Admin admin, ConstraintValidatorContext context) {
        if (admin == null) {
            return true;
        }

        UserType userType = admin.getUserType();
        
        buildViolations(context);
        return userType == UserType.INTERNAL;
    }
	
	private void buildViolations(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        
    	context.buildConstraintViolationWithTemplate(
		messageSource.getMessage(
	        	"ValidAdmin", 
	        	new Object[] {},
	        	LocaleContextHolder.getLocale()
	        )
        ).addConstraintViolation();
    }
}
