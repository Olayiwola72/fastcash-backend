package com.fastcash.moneytransfer.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.model.ExternalAccount;

class ExternalAccountRequestMapperTest {

	@Test
    void testToExternalAccount() {
        // Create MoneyTransferRequest instance using the static factory method
        MoneyTransferRequest request = MoneyTransferRequest.create("OWN_ACCOUNT", 1L, 0L, BigDecimal.TEN, "NGN", "NGN", BigDecimal.ONE, BigDecimal.TEN, null, "John Doe", "Bank Name");
        
        ExternalAccountRequestMapper requestMapper = new ExternalAccountRequestMapper();
        ExternalAccount externalAccount = requestMapper.toExternalAccount(request);
        
        // Assert that the instance is created successfully
        assertNotNull(externalAccount);
        assertEquals(externalAccount.getCurrency().toString(), request.creditCurrency());
        assertEquals(externalAccount.getAccountNumber(), request.creditAccount());
        assertNotEquals(externalAccount.getAccountNumber(), request.debitAccount());
        assertEquals(externalAccount.getAccountHolderName(), request.accountHolderName());
        assertEquals(externalAccount.getBankName(), request.bankName());

    }
}
