package com.fastcash.moneytransfer.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastcash.moneytransfer.annotation.ApiBaseUrlPrefix;
import com.fastcash.moneytransfer.dto.EnumResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@ApiBaseUrlPrefix
@RequestMapping("/enums")
@Tag(name = "Enum Controller", description = "Enum Controller API")
public class EnumController {
	@Operation(summary = "Get Default Enums")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Get Default Enums", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = EnumResponse.class)) }),
	})
	@SecurityRequirement(name = "Bearer Key")
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EnumResponse> get() {	
        return ResponseEntity.ok(new EnumResponse());
	}
}
