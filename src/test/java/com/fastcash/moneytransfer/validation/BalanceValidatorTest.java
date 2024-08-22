package com.fastcash.moneytransfer.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.exception.InsufficientBalanceException;
import com.fastcash.moneytransfer.model.UserAccount;

class BalanceValidatorTest {

	@Test
	void testWorkingBalanceLessThanAmount() throws InsufficientBalanceException {
		BigDecimal workingBalance = new BigDecimal(-1);
		
		UserAccount userAccount = new UserAccount();
		userAccount.setAllowOverdraft(false);
		
		assertThrows(
			InsufficientBalanceException.class, 
			() -> BalanceValidator.validateBalance(workingBalance, userAccount, BigDecimal.TEN)
		);
	}
	
	@Test
	void testWorkingBalanceEqualToAmount() throws InsufficientBalanceException {
		BigDecimal workingBalance = BigDecimal.TEN;
		
		UserAccount userAccount = new UserAccount();
		userAccount.setAllowOverdraft(false);
		
		assertDoesNotThrow(
			() -> BalanceValidator.validateBalance(workingBalance, userAccount, BigDecimal.TEN)
		);
	}
	
	@Test
	void testWorkingBalanceGreaterThanAmount() throws InsufficientBalanceException {
		BigDecimal workingBalance = BigDecimal.TEN.multiply(BigDecimal.TEN);
		
		UserAccount userAccount = new UserAccount();
		userAccount.setAllowOverdraft(false);
		
		assertDoesNotThrow(
			() -> BalanceValidator.validateBalance(workingBalance, userAccount, BigDecimal.TEN)
		);
	}
	
	@Test
	void testWorkingBalanceLessThanAmounAndOverdraftIsAllowed() throws InsufficientBalanceException {
		BigDecimal workingBalance = new BigDecimal(-1);
		
		UserAccount userAccount = new UserAccount();
		userAccount.setAllowOverdraft(true);
		
		assertDoesNotThrow(
			() -> BalanceValidator.validateBalance(workingBalance, userAccount, BigDecimal.TEN)
		);
	}

}
