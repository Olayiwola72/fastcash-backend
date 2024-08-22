package com.fastcash.moneytransfer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.enums.AuthMethod;
import com.fastcash.moneytransfer.enums.Currency;

class AdminTest {

	@Test
	void testAdminInitialization() {
		Admin admin = new Admin();
		
		assertNull(admin.getId()); // Id should not be null
        assertTrue(admin.isEnabled()); // Admin should be enabled
		assertEquals(0, admin.getInternalAccounts().size());
		assertEquals(0, admin.getChargeAccounts().size());
		assertEquals(0,admin.getAccountStatements().size());
        assertEquals(AuthMethod.ADMIN, admin.getAuthMethod());
        assertNotNull(admin.getCreatedAt()); // CreatedAt should not be null
	}
	
	@Test
	void testGettersAndSetters() {
		AccountStatement accountStatement = mock(AccountStatement.class);
		
		Admin admin = new Admin();
		admin.setInternalAccounts(List.of(new InternalAccount(Currency.NGN, admin)));
		admin.setChargeAccounts(List.of(new InternalChargeAccount(Currency.NGN, admin)));
		admin.setAccountStatements(List.of(accountStatement));
		
		assertEquals(1, admin.getInternalAccounts().size());
		assertEquals(1, admin.getChargeAccounts().size());
		assertEquals(1, admin.getAccountStatements().size());
	}

}
