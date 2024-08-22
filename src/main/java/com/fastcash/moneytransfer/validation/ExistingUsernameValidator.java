package com.fastcash.moneytransfer.validation;

import org.springframework.stereotype.Component;

import com.fastcash.moneytransfer.exception.UsernameAlreadyExistsException;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.service.UserService;

@Component
public class ExistingUsernameValidator {
	private final UserService userService;
	
	public ExistingUsernameValidator(UserService userService) {
		this.userService = userService;
	}
	
	public void isEmailExisting(String email) {
		User user = userService.isUserPresent(email);
		
        if(user != null) {
			throw new UsernameAlreadyExistsException(
    			"Email already exists", 
    			"DuplicateResource", 
    			new Object[]{
    				"Email", 
    				email
				},
    			"email"
    		);
		}
	}
}
