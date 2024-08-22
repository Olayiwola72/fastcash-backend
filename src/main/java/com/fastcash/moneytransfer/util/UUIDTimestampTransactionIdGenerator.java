package com.fastcash.moneytransfer.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class UUIDTimestampTransactionIdGenerator implements TransactionIdGenerator {
	
	@Override
	public String generateTransactionId() {
		long timestamp = System.currentTimeMillis(); // Current timestamp in milliseconds
	    String uniqueId = UUID.randomUUID().toString().substring(0, 6).toUpperCase(); // Unique ID generated using UUID
	    String transactionId = uniqueId + timestamp;
	    return transactionId;
	}
}
