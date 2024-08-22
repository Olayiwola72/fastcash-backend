package com.fastcash.moneytransfer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fastcash.moneytransfer.model.AccountStatement;
import com.fastcash.moneytransfer.repository.AccountStatementRepository;

@Service
@Transactional(propagation = Propagation.MANDATORY)
public class AccountStatementService {
	
	private final AccountStatementRepository accountStatementRepository;

    public AccountStatementService(AccountStatementRepository accountStatementRepository) {
        this.accountStatementRepository = accountStatementRepository;
    }
    
    public AccountStatement create(AccountStatement accountStatement) {
    	return accountStatementRepository.save(accountStatement);
    }
    
}