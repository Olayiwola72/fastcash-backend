package com.fastcash.moneytransfer.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.model.ExternalAccount;
import com.fastcash.moneytransfer.repository.ExternalAccountRepository;

@DataJpaTest
@Import({ 
	ExternalAccountService.class,
})
class ExternalAccountServiceTest {
	
	@Autowired
	private ExternalAccountRepository externalAccountRepository;
	
	@Autowired
	private ExternalAccountService externalAccountService;
	
	@Test 
	void testCreate(){
		ExternalAccount externalAccount = externalAccountService.create(new ExternalAccount(Currency.NGN, 1L, null, null));
		
		assertTrue(
			externalAccountRepository.findById(externalAccount.getId()).isPresent()
		);
	}
	
}
