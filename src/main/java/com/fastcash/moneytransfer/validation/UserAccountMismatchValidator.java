package com.fastcash.moneytransfer.validation;

import java.util.List;

import javax.annotation.Nonnull;

import com.fastcash.moneytransfer.exception.UserAccountMismatchException;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;

public class UserAccountMismatchValidator {
	
	public static void handleMismatch(@Nonnull UserAccount userAccount, @Nonnull User user, String fieldName) {
		if(userAccount == null || user == null || fieldName == null || fieldName.isBlank()) {
			handleException(userAccount, fieldName);
		}
		
		List<UserAccount> userAccounts = user.getAccounts();
		
		if(userAccounts != null) {
			for(UserAccount account : userAccounts) {
	        	if(account.getId().equals(userAccount.getId())) {
	        		return;
	        	}
	        }
		}
		
		handleException(userAccount, fieldName);
		
	}
	
	private static void handleException(UserAccount userAccount, String fieldName) {
		throw new UserAccountMismatchException(
			"User UserAccount Mismatch", 
			"UserAccountMismatch", 
			new Object[]{
				userAccount.getId().toString()
			},
			fieldName
		);
	}
}
