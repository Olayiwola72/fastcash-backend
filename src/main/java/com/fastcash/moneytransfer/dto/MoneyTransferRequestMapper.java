package com.fastcash.moneytransfer.dto;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fastcash.moneytransfer.model.MoneyTransfer;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.service.AccountService;
import com.fastcash.moneytransfer.service.impl.UUIDTimestampTransactionIdGenerator;

@Component
public class MoneyTransferRequestMapper {
	
	private final UUIDTimestampTransactionIdGenerator transactionIdGenerator;
	private final AccountService accountService;
	private final ExternalAccountRequestMapper externalAccountRequestMapper;
	
	public MoneyTransferRequestMapper(
		UUIDTimestampTransactionIdGenerator transactionIdGenerator, 
		AccountService accountService,
		ExternalAccountRequestMapper externalAccountRequestMapper
	) {
		this.transactionIdGenerator = transactionIdGenerator;
		this.accountService = accountService;
		this.externalAccountRequestMapper = externalAccountRequestMapper;
	}
	
	public MoneyTransfer toMoneyTransfer(User debitedUser, MoneyTransferRequest moneyTransferRequest) {
		UserAccount debitAccount = accountService.findById(moneyTransferRequest.debitAccount()).get();
		
		Currency creditCurrency = Currency.valueOf(moneyTransferRequest.creditCurrency());
	
        MoneyTransfer moneyTransfer = new MoneyTransfer();
    	moneyTransfer.setDebitedUser(debitedUser);
        moneyTransfer.setAmount(moneyTransferRequest.amount());
        moneyTransfer.setTransactionId(transactionIdGenerator.generateTransactionId());
        moneyTransfer.setDebitAccount(debitAccount);
        moneyTransfer.setDebitCurrency(Currency.valueOf(moneyTransferRequest.debitCurrency()));
        moneyTransfer.setNotes(moneyTransferRequest.notes());
        moneyTransfer.setTransactionType(TransactionType.valueOf(moneyTransferRequest.transactionType()));
        
        if(moneyTransfer.getTransactionType().isInternal()) {        	
        	Optional<UserAccount> creditAccountOptional = accountService.findById(moneyTransferRequest.creditAccount());
    		if(creditAccountOptional.isPresent()) {
    			moneyTransfer.setCreditAccount(creditAccountOptional.get());
    		}
        }else {
        	moneyTransfer.setCreditAccount(
				externalAccountRequestMapper.toExternalAccount(moneyTransferRequest)
			);
        }
		
        moneyTransfer.setCreditCurrency(creditCurrency);
        moneyTransfer.setTotalDebitedAmount(moneyTransferRequest.amount());
        moneyTransfer.setConversionRate(moneyTransferRequest.conversionRate());
        moneyTransfer.setTotalCreditedAmount(moneyTransferRequest.conversionAmount());
        
        return moneyTransfer;
    }

}
