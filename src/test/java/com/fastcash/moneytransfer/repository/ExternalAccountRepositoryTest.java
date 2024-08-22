package com.fastcash.moneytransfer.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.model.ExternalAccount;

@DataJpaTest
class ExternalAccountRepositoryTest {

	@Autowired
    private ExternalAccountRepository externalAccountRepository;
	
	private final Currency currency = Currency.NGN;
	private final Long accountNumber = 123L;
	private final String accountHolderName = "accountHolderName";
	private final String bankName = "bankName";
	private ExternalAccount externalAccount;
	
	@BeforeEach
	void setUp() {
		externalAccount = new ExternalAccount(Currency.NGN, 123L,"accountHolderName", "bankName");
		externalAccountRepository.save(externalAccount);
	}
	
	@AfterEach
	void tearDown(){
		externalAccountRepository.delete(externalAccount);
	}

    @Test
    public void testFindByEmail() {
        // When
        Optional<ExternalAccount> foundExternalAccount = externalAccountRepository.findByAccountNumber(accountNumber);

        // Then
        assertTrue(foundExternalAccount.isPresent());
        assertEquals(currency, foundExternalAccount.get().getCurrency());
        assertEquals(accountHolderName, foundExternalAccount.get().getAccountHolderName());
        assertEquals(bankName, foundExternalAccount.get().getBankName());
    }

    @Test
    public void testFindByEmailNotFound() {
        // When
    	Optional<ExternalAccount> foundExternalAccount = externalAccountRepository.findByAccountNumber(0L);

        // Then
        assertFalse(foundExternalAccount.isPresent());
    }

}
