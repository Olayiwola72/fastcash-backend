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
		EnumResponse enumResponse = new EnumResponse();
		
		assertEquals(DateFormatter.today(), enumResponse.todayDate());
		assertEquals(Currency.values().length, enumResponse.currencies().size());
		assertEquals(AuthMethod.values().length, enumResponse.providers().length);
		assertEquals(TransactionType.values().length, enumResponse.transactionTypes().length);
	}
}
