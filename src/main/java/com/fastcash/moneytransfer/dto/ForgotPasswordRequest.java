package com.fastcash.moneytransfer.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
		@Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "user email", example = "test@moneytransfer.com")
		@NotBlank
		@Email
		String email
	) {
	
	@JsonCreator
    public static ForgotPasswordRequest create(
       @JsonProperty("email") String email
       ) {
			
		return new ForgotPasswordRequest(email);
    }
}
