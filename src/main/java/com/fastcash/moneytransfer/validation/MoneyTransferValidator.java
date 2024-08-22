package com.fastcash.moneytransfer.validation;

import java.math.BigDecimal;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.fastcash.moneytransfer.annotation.ValidMoneyTransfer;
import com.fastcash.moneytransfer.config.MessageSourceConfig;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fastcash.moneytransfer.model.InternalAccount;
import com.fastcash.moneytransfer.model.InternalChargeAccount;
import com.fastcash.moneytransfer.model.MoneyTransfer;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MoneyTransferValidator implements ConstraintValidator<ValidMoneyTransfer, MoneyTransfer> {
	
	private ReloadableResourceBundleMessageSource messageSource;
	
	public MoneyTransferValidator() {
		messageSource = new MessageSourceConfig().messageSource();
	}
	
	@Override
	public void initialize(ValidMoneyTransfer constraintAnnotation) {

	}

	@Override
	public boolean isValid(MoneyTransfer moneyTransfer, ConstraintValidatorContext context) {
		if (moneyTransfer == null) {
			return true;
		}

		context.disableDefaultConstraintViolation();
		boolean isValid = true;

		TransactionType type = moneyTransfer.getTransactionType();
		InternalAccount internalAccount = moneyTransfer.getInternalAccount();
		InternalChargeAccount internalChargeAccount = moneyTransfer.getInternalChargeAccount();
		BigDecimal chargeAmount = moneyTransfer.getChargeAmount();
		BigDecimal rate = moneyTransfer.getConversionRate();

		switch (type) {
			case OWN_ACCOUNT:
				if (internalAccount != null || internalChargeAccount != null || chargeAmount.compareTo(BigDecimal.ZERO) != 0) {
					isValid = false;
					
			    	context.buildConstraintViolationWithTemplate(
			    		messageSource.getMessage(
				        	"OwnAccountTransactionMismatch", 
				        	new Object[] {
				        		type,
				        	},
				        	LocaleContextHolder.getLocale()
				        )
			        )
			    	.addConstraintViolation();
				}
				break;
			case INTER_BANK:			
			case INTERNATIONAL:
				if (internalAccount == null || internalChargeAccount == null || chargeAmount.compareTo(BigDecimal.ZERO) == 0) {
					isValid = false;
					
					context.buildConstraintViolationWithTemplate(
			    		messageSource.getMessage(
				        	"InterAccountTransactionMismatch", 
				        	new Object[] {
				        		type,
				        	},
				        	LocaleContextHolder.getLocale()
				        )
			        )
			    	.addConstraintViolation();
				}
				break;
			default:
				break;
		}

		if (moneyTransfer.getDebitCurrency().equals(moneyTransfer.getCreditCurrency()) && rate.compareTo(BigDecimal.ONE) != 0) {
			isValid = false;
			
			context.buildConstraintViolationWithTemplate(
	    		messageSource.getMessage(
		        	"ConversionRateMismatch", 
		        	new Object[] {
		        		"same-currency",
		        	},
		        	LocaleContextHolder.getLocale()
		        )
	        )
	    	.addConstraintViolation();
		}
		
		return isValid;
	}
}
