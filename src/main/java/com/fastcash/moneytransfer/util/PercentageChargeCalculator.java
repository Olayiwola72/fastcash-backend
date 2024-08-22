package com.fastcash.moneytransfer.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import com.fastcash.moneytransfer.constant.Constants;

@Component
public class PercentageChargeCalculator implements ChargeCalculator {
	
    @Override
    public BigDecimal calculateCharge(BigDecimal amount, BigDecimal chargePercentage) {
        BigDecimal chargeAmount = amount.multiply(chargePercentage).setScale(Constants.AMOUNT_SCALE, RoundingMode.UP);
        return chargeAmount;
    }
    
}