package com.fastcash.moneytransfer.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.enums.AccountCategory;

class PercentageChargeCalculatorTest {

	@Test
	void testCalculateCharge() {
		BigDecimal actualChargeAmount = new BigDecimal("0.5").setScale(Constants.AMOUNT_SCALE, RoundingMode.UP);
		
		ChargeCalculator chargeCalculator = new PercentageChargeCalculator();
		BigDecimal expectedChargeAmount = chargeCalculator.calculateCharge(BigDecimal.TEN, AccountCategory.USER_ACCOUNT.getChargeAmount());
		
		assertEquals(actualChargeAmount, expectedChargeAmount);
	}

}
