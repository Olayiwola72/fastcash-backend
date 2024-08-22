package com.fastcash.moneytransfer.util;

import java.math.BigDecimal;

public interface ChargeCalculator {
    BigDecimal calculateCharge(BigDecimal amount, BigDecimal chargePercentage);
}
