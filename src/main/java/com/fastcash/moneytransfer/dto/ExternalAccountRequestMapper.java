package com.fastcash.moneytransfer.dto;

import org.springframework.stereotype.Component;

import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.model.ExternalAccount;

@Component
public class ExternalAccountRequestMapper {
	
	public ExternalAccount toExternalAccount(MoneyTransferRequest request) {
		return new ExternalAccount(
			Currency.valueOf(request.creditCurrency()), 
			request.creditAccount(), 
			request.accountHolderName(), 
			request.bankName()
		);
	}
	
}