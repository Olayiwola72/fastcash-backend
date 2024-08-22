package com.fastcash.moneytransfer.util;

import com.fastcash.moneytransfer.constant.Constants;

public class DataMasker {
	
	private static final String accountMask = Constants.ACCOUNT_MASK;
	
	public static String maskAccountId(String accountId) {
        if (accountId == null) {
            return null;
        }
        if (accountId.length() <= accountMask.length()) {
            return accountMask;
        }
        
        return accountMask + accountId.substring(accountMask.length());
    }
}
