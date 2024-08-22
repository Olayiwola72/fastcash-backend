package com.fastcash.moneytransfer.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidIDTokenException extends AuthenticationException {
	private static final long serialVersionUID = 1L;
    
    public InvalidIDTokenException(String message) {
        super(message);
    }
}