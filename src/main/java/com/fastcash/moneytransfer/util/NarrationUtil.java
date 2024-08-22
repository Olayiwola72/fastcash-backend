package com.fastcash.moneytransfer.util;

import com.fastcash.moneytransfer.enums.TransactionType;

public class NarrationUtil {

	public static  String getNarration(TransactionType transactionType, String notes) {
		if(notes == null || notes.isBlank()) {
    		return transactionType.getDescription();
    	}else {
    		return transactionType.getDescription() + ", "+ notes;
    	}
	}

}
