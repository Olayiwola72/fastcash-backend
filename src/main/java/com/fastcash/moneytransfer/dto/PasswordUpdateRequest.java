package com.fastcash.moneytransfer.dto;

import com.fastcash.moneytransfer.annotation.ValidPassword;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PasswordUpdateRequest(
		@Schema(accessMode = Schema.AccessMode.WRITE_ONLY, description = "user password", example = "P@ssword12$")
		@NotBlank
		@ValidPassword
		String password
	) {
	
	@JsonCreator
    public static PasswordUpdateRequest create(
       @JsonProperty("password") String password
       ) {
			
		return new PasswordUpdateRequest(password);
    }
}
