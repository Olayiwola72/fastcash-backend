package com.fastcash.moneytransfer.dto;

import java.util.List;

import com.fastcash.moneytransfer.enums.AuthMethod;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fastcash.moneytransfer.util.DateFormatter;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record EnumResponse(
		@NotNull
		@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "today's date dd/MM/yyyy")
		String todayDate,
		
		@NotNull
		@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "currencies")
		List<CurrencyResponse> currencies,
		
		@NotNull
		@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "providers")
		AuthMethod[] providers,
		
		@NotNull
		@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "transactionTypes")
		TransactionType[] transactionTypes
		
	) {
	
	// Default constructor with default values
    public EnumResponse() {
        this(
        	DateFormatter.today(),
    		Currency.getCurrencyResponse(), 
    		AuthMethod.values(),
    		TransactionType.values()
    	);
    }
    
}
