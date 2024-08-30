package com.fastcash.moneytransfer.controller;

import java.math.BigDecimal;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fastcash.moneytransfer.annotation.ApiBaseUrlPrefix;
import com.fastcash.moneytransfer.dto.ExchangeRateResponse;
import com.fastcash.moneytransfer.service.ExchangeRateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@ApiBaseUrlPrefix
@RequestMapping("${app.api.exchange-rate-path}")
@Tag(name = "Exchange Rate Controller", description = "Exchange Rate Controller API")
public class ExchangeRateController {
	private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }
    
    @Operation(summary = "Get Exchange Rate")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Get Exchange Rate", content = {
    	@Content(mediaType = "application/json", schema = @Schema(implementation = ExchangeRateResponse.class)) }),
    })
    @SecurityRequirement(name = "Bearer Key")
    @PreAuthorize("hasRole('USER')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)	
    public ResponseEntity<ExchangeRateResponse> getExchangeRate(
    		@Parameter(description = "Base Currency", required = true) @RequestParam String baseCurrency,
    		@Parameter(description = "Target Currency", required = true) @RequestParam String targetCurrency,
    		@Parameter(description = "Amount", required = false) @RequestParam(required = false) BigDecimal amount) {
    	
    	ExchangeRateResponse exchangeRateResponse;
    	
    	if(amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
    		exchangeRateResponse = exchangeRateService.getExchangeAmount(baseCurrency, targetCurrency, amount);
    	}else {
    		exchangeRateResponse = exchangeRateService.getExchangeRate(baseCurrency, targetCurrency);
    	}
    	
    	return ResponseEntity.ok(exchangeRateResponse);
    }
}
