package com.fastcash.moneytransfer.dto;

import com.fastcash.moneytransfer.annotation.ValidPassword;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
		@NotBlank
		@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "auth token", example = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic.3ViIjoi")
		String token,
		
		@Schema(accessMode = Schema.AccessMode.WRITE_ONLY, description = "user password", example = "P@ssword12$")
		@NotBlank
		@ValidPassword
		String password
	) {
	
	@JsonCreator
    public static ResetPasswordRequest create(
		@JsonProperty("token") String token,
		@JsonProperty("password") String password
    ) {
			
		return new ResetPasswordRequest(token, password);
    }
	
}
