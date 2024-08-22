package com.fastcash.moneytransfer.controller;

import java.util.Optional;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastcash.moneytransfer.annotation.ApiBaseUrlPrefix;
import com.fastcash.moneytransfer.dto.APIResponse;
import com.fastcash.moneytransfer.dto.MoneyTransferRequest;
import com.fastcash.moneytransfer.dto.MoneyTransferRequestMapper;
import com.fastcash.moneytransfer.dto.UserResponse;
import com.fastcash.moneytransfer.exception.InsufficientBalanceException;
import com.fastcash.moneytransfer.model.MoneyTransfer;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.service.MoneyTransferService;
import com.fastcash.moneytransfer.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@ApiBaseUrlPrefix
@Validated
@RequestMapping("${endpoint.transfer}")
@Tag(name = "Transfer Controller", description = "Transfer Controller API")
public class MoneyTransferController {
	private final MoneyTransferService moneyTransferService;
	private final UserService userService;
	private final MoneyTransferRequestMapper moneyTransferRequestMapper;
	private final ReloadableResourceBundleMessageSource messageSource;
	
	public MoneyTransferController(
		MoneyTransferService moneyTransferService, 
		UserService userService, 
		MoneyTransferRequestMapper moneyTransferRequestMapper,
		ReloadableResourceBundleMessageSource messageSource
	) {
		this.moneyTransferService = moneyTransferService;
		this.userService = userService;
		this.moneyTransferRequestMapper = moneyTransferRequestMapper;
		this.messageSource = messageSource;
	}
	
	@Operation(summary = "Create Transfer")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Transaction created successfully", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class)) }),
	})
	@SecurityRequirement(name = "Bearer Key")
	@PreAuthorize("hasRole('USER')")
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<APIResponse> create(@Valid @RequestBody MoneyTransferRequest moneyTransferRequest, Authentication authentication) throws InsufficientBalanceException {
		
		Optional<User> optionalUser = userService.findByEmail(authentication.getName());
		User user = optionalUser.get();
		
		MoneyTransfer moneyTransfer = moneyTransferRequestMapper.toMoneyTransfer(user, moneyTransferRequest);
		
		moneyTransferService.create(moneyTransfer, user);
		
		user = userService.findById(user.getId()).get();
		
		String errorMessage = messageSource.getMessage("TransferSuccess", 
			new Object[]{
					moneyTransfer.getTransactionId(), 
			}, 
			LocaleContextHolder.getLocale()
		);
        
        APIResponse apiResponse = new APIResponse(
        	errorMessage, 
    		null, 
    		null,
    		new UserResponse(user)
    	);
        
        return ResponseEntity.ok(apiResponse);
	}
}
