package com.fastcash.moneytransfer.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.enums.AuthMethod;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fastcash.moneytransfer.util.DateFormatter;

class EnumResponseTest {
	
	@Test
	void testEnumResponseDefaultConstructor() {
		ConfigResponse configResponse = new ConfigResponse();
		
		assertEquals(DateFormatter.today(), configResponse.todayDate());
		assertEquals(Currency.values().length, configResponse.currencies().size());
		assertEquals(AuthMethod.values().length, configResponse.providers().length);
		assertEquals(TransactionType.values().length, configResponse.transactionTypes().length);
	}
}
