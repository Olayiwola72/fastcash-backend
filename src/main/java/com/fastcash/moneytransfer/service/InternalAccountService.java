package com.fastcash.moneytransfer.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.model.Admin;
import com.fastcash.moneytransfer.model.InternalAccount;
import com.fastcash.moneytransfer.repository.InternalAccountRepository;

@Service
public class InternalAccountService {
    private final InternalAccountRepository internalAccountRepository;

    public InternalAccountService(InternalAccountRepository internalAccountRepository) {
        this.internalAccountRepository = internalAccountRepository;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public List<InternalAccount> create(Admin admin){
        List<InternalAccount> internalAccounts = new ArrayList<>();
        
        for(Currency currency : Currency.values()) {
        	InternalAccount chargeAccount = new InternalAccount(currency, admin);
        	internalAccounts.add(chargeAccount);
        }
        
        return internalAccountRepository.saveAll(internalAccounts);
    }
    
}
