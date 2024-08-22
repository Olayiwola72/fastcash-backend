package com.fastcash.moneytransfer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.model.Admin;
import com.fastcash.moneytransfer.model.InternalAccount;
import com.fastcash.moneytransfer.repository.InternalAccountRepository;

@DataJpaTest
@Import({ 
	InternalAccountService.class,
})
class InternalAccountServiceTest {
	
	@Autowired
    private TestEntityManager entityManager;
	
	@Autowired
	private InternalAccountRepository internalAccountRepository;
	
	@Autowired
	private InternalAccountService internalAccountService;
	
	private Admin admin;
	
	@BeforeEach
    void setUp() {
		admin = new Admin();
		admin.setEmail("user@example.com");
		admin.setRoles("USER");
        admin.setPassword("password");
        entityManager.persist(admin);
        entityManager.flush();
    }
	
	
	@Test 
	void testCreate(){
		List<InternalAccount> createdAccounts = internalAccountService.create(admin);
	    
	    // Assert that the number of created accounts matches the number of currencies
	    assertEquals(Currency.values().length, createdAccounts.size());
	    
	    // Retrieve all user accounts from the repository
	    List<InternalAccount> allChargeAccounts = internalAccountRepository.findAll();
	    
	    // Assert that each created account is present in the repository
	    for (InternalAccount createdAccount : createdAccounts) {
	        assertTrue(allChargeAccounts.contains(createdAccount), "Internal account should be present in the repository");
	    }
	}
	
}
