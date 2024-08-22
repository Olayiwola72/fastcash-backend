package com.fastcash.moneytransfer.enums;

import java.math.BigDecimal;

public enum AccountCategory {
	
	USER_ACCOUNT (new BigDecimal("0.05")),
	INTERNAL_ACCOUNT(BigDecimal.ZERO);
	
    private final BigDecimal chargeAmount;

    AccountCategory(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }
    
}
