package com.fastcash.moneytransfer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "API Response Information")
public record APIResponse(
	@NotEmpty
	@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "success message", example = "signed in successfully")
	String successMessage,
		
	@NotEmpty
	@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "auth token", example = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic.3ViIjoi")
	String token,
	
	@NotBlank
	@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "auth token", example = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic.3ViIjoi")
	String refreshToken,
	
	@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "User Response Information")
	UserResponse userData
	) {
}
