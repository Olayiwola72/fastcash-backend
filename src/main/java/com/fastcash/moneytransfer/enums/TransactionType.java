package com.fastcash.moneytransfer.enums;

public enum TransactionType {
	
	OWN_ACCOUNT ("Own Account Transfer", true),
	ACCOUNT_TO_ACCOUNT ("Account to Account Transfer", true),
	INTER_BANK ("Inter Bank Transfer", false),
	INTERNATIONAL ("International Transfer", false);
	
	private final String description;
	private final boolean isInternal;
	
	TransactionType(String description, boolean isInternal) {
		this.description = description;
		this.isInternal = isInternal;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean isInternal() {
		return isInternal;
	}
	
}
