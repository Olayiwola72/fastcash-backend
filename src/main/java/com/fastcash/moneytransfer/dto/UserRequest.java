package com.fastcash.moneytransfer.dto;

import com.fastcash.moneytransfer.annotation.ValidPassword;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
		@Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "user email", example = "test@moneytransfer.com")
		@NotBlank
		@Email
		String email,
		
		@Schema(accessMode = Schema.AccessMode.WRITE_ONLY, description = "user password", example = "P@ssword12$")
		@NotBlank
		@ValidPassword
		String password
	) {
	
	@JsonCreator
    public static UserRequest create(
       @JsonProperty("email") String email,
       @JsonProperty("password") String password
       ) {
			
		return new UserRequest(email, password);
    }
}
