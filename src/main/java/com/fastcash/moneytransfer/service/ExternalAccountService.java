package com.fastcash.moneytransfer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fastcash.moneytransfer.model.ExternalAccount;
import com.fastcash.moneytransfer.repository.ExternalAccountRepository;

@Service
public class ExternalAccountService {
    private final ExternalAccountRepository externalAccountRepository;

    public ExternalAccountService(ExternalAccountRepository externalAccountRepository) {
        this.externalAccountRepository = externalAccountRepository;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public ExternalAccount create(ExternalAccount externalAccount){
        return externalAccountRepository.save(externalAccount);
    }
}
