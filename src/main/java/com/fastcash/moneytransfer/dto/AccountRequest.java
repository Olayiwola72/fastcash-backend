package com.fastcash.moneytransfer.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AccountRequest(
		@Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "allow overdraft", example = "true")
		@NotNull
		Boolean allowOverdraft
	) {
	
	@JsonCreator
    public static AccountRequest create(
       @JsonProperty("allowOverdraft") Boolean allowOverdraft
       ) {
			
		return new AccountRequest(allowOverdraft);
    }
}


