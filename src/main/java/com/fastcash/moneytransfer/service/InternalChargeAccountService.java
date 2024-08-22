package com.fastcash.moneytransfer.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.model.Admin;
import com.fastcash.moneytransfer.model.InternalChargeAccount;
import com.fastcash.moneytransfer.repository.InternalChargeAccountRepository;

@Service
public class InternalChargeAccountService {
    private final InternalChargeAccountRepository internalChargeAccountRepository;

    public InternalChargeAccountService(InternalChargeAccountRepository internalChargeAccountRepository) {
        this.internalChargeAccountRepository = internalChargeAccountRepository;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public List<InternalChargeAccount> create(Admin admin){
        List<InternalChargeAccount> chargeAccounts = new ArrayList<>();
        
        for(Currency currency : Currency.values()) {
        	InternalChargeAccount chargeAccount = new InternalChargeAccount(currency, admin);
        	chargeAccounts.add(chargeAccount);
        }
        
        return internalChargeAccountRepository.saveAll(chargeAccounts);
    }

}
