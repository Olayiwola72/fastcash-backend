package com.fastcash.moneytransfer.dto;

import java.math.BigDecimal;

import com.fastcash.moneytransfer.model.AccountStatement;

import io.swagger.v3.oas.annotations.media.Schema;

public class AccountStatementResponse extends BaseTransferResponse {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "direction", example = "Receiving or Sending")
    private String direction;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "direction", example = "+ or -")
    private char sign;
    
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "transaction balance", example = "10.000")
    private BigDecimal balance;

    public AccountStatementResponse(AccountStatement accountStatement) {
        super();
        populateCommonFields(accountStatement);
        this.direction = accountStatement.getDirection().getDescription();
        this.sign = accountStatement.getDirection().getSign();
        this.balance = accountStatement.getBalance();
    }

    // Getters and setters specific to AccountStatementResponse
    public String getDirection() {
        return direction;
    }

    public char getSign() {
        return sign;
    }

	public BigDecimal getBalance() {
		return balance;
	}
    
}
