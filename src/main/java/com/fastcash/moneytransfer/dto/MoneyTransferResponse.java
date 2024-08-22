package com.fastcash.moneytransfer.dto;

import com.fastcash.moneytransfer.model.MoneyTransfer;

public class MoneyTransferResponse extends BaseTransferResponse {

    public MoneyTransferResponse(MoneyTransfer moneyTransfer) {
        super();
        populateCommonFields(moneyTransfer);
    }
    
}