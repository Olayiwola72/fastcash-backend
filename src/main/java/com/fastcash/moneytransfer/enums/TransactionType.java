package com.fastcash.moneytransfer.enums;

public enum TransactionType {
	
	OWN_ACCOUNT ("Own Account Transfer"),
	INTER_BANK ("Inter Bank Transfer"),
	INTERNATIONAL ("International Transfer");
	
	private final String description;
	
	TransactionType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
}
