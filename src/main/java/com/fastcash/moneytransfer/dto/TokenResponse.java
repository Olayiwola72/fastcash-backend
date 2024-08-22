package com.fastcash.moneytransfer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Token Response Information")
public record TokenResponse(
	
	@NotBlank
	@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "auth token", example = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic.3ViIjoi")
	String token,
	
	@NotBlank
	@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "referesh token", example = "f0bf0001-649d-49c5-b0e5-d68f6bab583a")
	String refreshToken
	) {
	
}