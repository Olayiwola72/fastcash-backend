package com.fastcash.moneytransfer.dto;

import java.math.BigDecimal;

import com.fastcash.moneytransfer.annotation.CurrencyMismatch;
import com.fastcash.moneytransfer.annotation.DebitCreditAccountNotEqual;
import com.fastcash.moneytransfer.annotation.ValidAccount;
import com.fastcash.moneytransfer.annotation.ValidEnum;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@DebitCreditAccountNotEqual
@CurrencyMismatch
public record MoneyTransferRequest(
        @NotBlank
        @ValidEnum(enumClass = TransactionType.class)
        @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "transactionType", example = "OWN_ACCOUNT")
        String transactionType,
        
        @NotNull
        @ValidAccount
        @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "debit account", example = "10002")
        Long debitAccount,
        
        @NotNull
        @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "credit account", example = "10003")
        Long creditAccount,
        
        @NotNull
        @DecimalMin(value = "0", inclusive = false)
        @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "amount", example = "10.000")
        BigDecimal amount,
        
        @NotEmpty
        @NotNull
        @ValidEnum(enumClass = Currency.class)
        @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "currency", example = "NGN")
        String debitCurrency,
        
        @NotEmpty
        @NotNull
        @ValidEnum(enumClass = Currency.class)
        @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "currency", example = "USD")
        String creditCurrency,
        
        @NotNull
        @DecimalMin(value = "0", inclusive = false)
        @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "conversion rate", example = "0.01")
        BigDecimal conversionRate,
        
        @NotNull
        @DecimalMin(value = "0", inclusive = false)
        @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "conversion amount", example = "10")
        BigDecimal conversionAmount,
        
        @Size(max = 35)
        @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "notes", example = "this is a test transfer")
        String notes,
        
        @Size(max = 35)
        @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "accountHolderName", example = "John Doe")
        String accountHolderName,
        
        @Size(max = 35)
        @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "bankName", example = "Test Bank")
        String bankName
) {
    
    @JsonCreator
    public static MoneyTransferRequest create(
            @JsonProperty("transactionType") String transactionType,
            @JsonProperty("debitAccount") Long debitAccount,
            @JsonProperty("creditAccount") Long creditAccount,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("debitCurrency") String debitCurrency,
            @JsonProperty("creditCurrency") String creditCurrency,
            @JsonProperty("conversionRate") BigDecimal conversionRate,
            @JsonProperty("conversionAmount") BigDecimal conversionAmount,
            @JsonProperty("notes") String notes,
            @JsonProperty("accountHolderName") String accountHolderName,
            @JsonProperty("bankName") String bankName
    ) {
        return new MoneyTransferRequest(transactionType, debitAccount, creditAccount, amount, debitCurrency, creditCurrency, conversionRate, conversionAmount, notes, accountHolderName, bankName);
    }
}


