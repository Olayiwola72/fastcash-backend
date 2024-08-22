package com.fastcash.moneytransfer.enums;

public enum TransactionDirection {
	
	DEBIT("Sending", '-'),
	CREDIT("Receiving", '+');
	
	private final String description;
	private final char sign;
	
	TransactionDirection(String description, char sign) {
		this.description = description;
		this.sign = sign;
	}

	public String getDescription() {
		return description;
	}

	public char getSign() {
		return sign;
	}
	
}
