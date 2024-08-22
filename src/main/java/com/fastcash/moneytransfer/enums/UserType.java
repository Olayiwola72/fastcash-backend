package com.fastcash.moneytransfer.enums;

public enum UserType {
	
	INTERNAL(true),
	EXTERNAL(false),
	LINKED(true);
	
	private final boolean internal;
	
	UserType(boolean internal) {
		this.internal = internal;
	}

	public boolean isInternal() {
		return internal;
	}
	
}
