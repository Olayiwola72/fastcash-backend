package com.fastcash.moneytransfer.exception;

import java.math.BigDecimal;

import com.fastcash.moneytransfer.model.UserAccount;

public class InsufficientBalanceException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private final String code;
	private final UserAccount userAccount;
	private final BigDecimal amount;
	private final String fieldName;
    
    public InsufficientBalanceException(String message, String code, UserAccount userAccount, BigDecimal amount, String fieldName) {
        super(message);
        this.code = code;
        this.userAccount = userAccount;
        this.amount = amount;
        this.fieldName = fieldName;
    }
    
	public UserAccount getAccount() {
		return userAccount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getCode() {
		return code;
	}
}
