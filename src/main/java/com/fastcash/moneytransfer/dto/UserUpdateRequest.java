package com.fastcash.moneytransfer.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
		@Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "user name", example = "John Doe")
		@NotBlank
		@Size(max = 35)
		String name
	) {
	
	@JsonCreator
    public static UserUpdateRequest create(
       @JsonProperty("name") String name
       ) {
			
		return new UserUpdateRequest(name);
    }
}
