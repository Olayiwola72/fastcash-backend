package com.fastcash.moneytransfer.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastcash.moneytransfer.annotation.ApiBaseUrlPrefix;
import com.fastcash.moneytransfer.dto.ConfigResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@ApiBaseUrlPrefix
@RequestMapping("${app.api.config-path}")
@Tag(name = "Config Controller", description = "Config Controller API")
public class ConfigController {
	@Operation(summary = "Get Default Config")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Get Default Config", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = ConfigResponse.class)) }),
	})
	@SecurityRequirement(name = "Bearer Key")
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ConfigResponse> get() {	
        return ResponseEntity.ok(new ConfigResponse());
	}
}
