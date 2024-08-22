package com.fastcash.moneytransfer.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(
		@Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "referesh token", example = "f0bf0001-649d-49c5-b0e5-d68f6bab583a")
		@NotBlank
		String refreshToken
	) {
	
	@JsonCreator
    public static TokenRefreshRequest create(
       @JsonProperty("refreshToken") String refreshToken
       ) {
			
		return new TokenRefreshRequest(refreshToken);
    }
}
