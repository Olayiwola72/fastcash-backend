package com.fastcash.moneytransfer.validation;

import java.math.BigDecimal;

import com.fastcash.moneytransfer.exception.InsufficientBalanceException;
import com.fastcash.moneytransfer.model.UserAccount;

public class BalanceValidator {
	public static void validateBalance(BigDecimal workingBalance, UserAccount userAccount, BigDecimal amount)
			throws InsufficientBalanceException {
		if (workingBalance.compareTo(BigDecimal.ZERO) <= 0 && !userAccount.isAllowOverdraft()) {
			// If workingBalance is less than 0, throw InsufficientBalanceException
			throw new InsufficientBalanceException("Insufficient Balance", "InsufficientBalance", userAccount, amount,
					"debitAccount");
		}
	}
}
