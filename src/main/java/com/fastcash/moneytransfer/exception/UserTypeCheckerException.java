package com.fastcash.moneytransfer.exception;

import org.springframework.security.core.AuthenticationException;

public class UserTypeCheckerException extends AuthenticationException {
	private static final long serialVersionUID = 1L;
    
    public UserTypeCheckerException(String message) {
        super(message);
    }
}