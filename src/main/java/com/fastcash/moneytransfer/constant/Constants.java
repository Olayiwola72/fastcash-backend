package com.fastcash.moneytransfer.constant;

public class Constants {
	public static final String JWT_ISSUER = "http://self";
	
	public static final int AMOUNT_SCALE = 3;
	
	public static final int BALANCE_SCALE = 3;
	
	public static final String ACCOUNT_MASK = "**";
	
	public static final int PASSWORD_RESET_TOKEN_EXPIRY_HOURS = 24;
	
	// Private constructor to prevent instantiation
    private Constants() {
        // Prevent instantiation
    }
}
