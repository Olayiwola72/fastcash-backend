package com.fastcash.moneytransfer.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.exception.InsufficientBalanceException;
import com.fastcash.moneytransfer.exception.MissingInternalAccountException;
import com.fastcash.moneytransfer.exception.MissingInternalChargeAccountException;
import com.fastcash.moneytransfer.model.Admin;
import com.fastcash.moneytransfer.model.ExternalAccount;
import com.fastcash.moneytransfer.model.InternalAccount;
import com.fastcash.moneytransfer.model.InternalChargeAccount;
import com.fastcash.moneytransfer.model.MoneyTransfer;
import com.fastcash.moneytransfer.model.TransactionAccount;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.util.PercentageChargeCalculator;

@Service
public class InternalMoneyTransferService {

    private final AccountService accountService;
    private final PercentageChargeCalculator chargeCalculator;
	private final ExternalAccountService externalAccountService;

    public InternalMoneyTransferService(
    	AccountService accountService,
    	PercentageChargeCalculator chargeCalculator,
		ExternalAccountService externalAccountService
    ){
        this.accountService = accountService;
        this.chargeCalculator = chargeCalculator;
        this.externalAccountService = externalAccountService;
    }
    
    @Transactional(
        rollbackFor = Exception.class, 
        propagation = Propagation.REQUIRED, 
        isolation = Isolation.SERIALIZABLE
    )
    public MoneyTransfer handleInternalTransfer(UserAccount debitAccount, MoneyTransfer moneyTransfer, Admin adminUser) throws InsufficientBalanceException {
    	
    	InternalAccount internalAccount = getCurrencyAccount(moneyTransfer.getCreditCurrency(), adminUser.getInternalAccounts());
        InternalChargeAccount chargeAccount = getCurrencyAccount(moneyTransfer.getDebitCurrency(), adminUser.getChargeAccounts());
		
        handleDebitAccount(debitAccount, moneyTransfer);
        handleInternalAccount(internalAccount, moneyTransfer);
        handleInternalChargeAccount(chargeAccount, moneyTransfer);
        
		externalAccountService.create((ExternalAccount) moneyTransfer.getCreditAccount());
        
        return moneyTransfer;
    }
    
    
    private void handleDebitAccount(UserAccount debitAccount, MoneyTransfer moneyTransfer) throws InsufficientBalanceException {
		BigDecimal chargeAmount = chargeCalculator.calculateCharge(
			debitAccount.getAccountCategory().getChargeAmount(), 
			moneyTransfer.getTotalDebitedAmount()
		);		
		BigDecimal totalDebitedAmount = moneyTransfer.getTotalDebitedAmount().add(chargeAmount);
		
		moneyTransfer.setChargeAmount(chargeAmount);
		moneyTransfer.setTotalDebitedAmount(totalDebitedAmount);
		
		// update debit account balance  
        accountService.withdraw(debitAccount, moneyTransfer.getTotalDebitedAmount());
    }
    
    private void handleInternalAccount(InternalAccount internalAccount, MoneyTransfer moneyTransfer) {
    	if(internalAccount != null) {
        	// update credit account balance
            accountService.deposit(internalAccount, moneyTransfer.getTotalCreditedAmount());
        	
        	moneyTransfer.setInternalAccount(internalAccount);
        }else {
        	throw new MissingInternalAccountException(
        		"Missing internal account", 
        		"MissingInternalAccount", 
        		new Object[] {
        				moneyTransfer.getCreditCurrency()	
        		}, 
        		"creditCurrency"
        	);
        }
    }
    
    private void handleInternalChargeAccount(InternalChargeAccount chargeAccount, MoneyTransfer moneyTransfer) {
    	if(chargeAccount != null) {
        	// update credit account balance
            accountService.deposit(chargeAccount, moneyTransfer.getChargeAmount());
        	
        	moneyTransfer.setInternalChargeAccount(chargeAccount);
        }else {
        	throw new MissingInternalChargeAccountException(
        		"Missing internal charge account", 
        		"MissingInternalChargeAccount", 
        		new Object[] {
        				moneyTransfer.getDebitCurrency()	
        		}, 
        		"debitCurrency"
        	);
        }
    }
    
    private <T extends TransactionAccount> T getCurrencyAccount(Currency currency, List<T> accounts) { 	
        if (accounts == null) return null;
        
        return accounts.stream()
            .filter(account -> account.getCurrency().equals(currency))
            .findFirst()
            .orElse(null);
    }

    
}

