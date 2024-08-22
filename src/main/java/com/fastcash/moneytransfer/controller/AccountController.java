package com.fastcash.moneytransfer.controller;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastcash.moneytransfer.annotation.ApiBaseUrlPrefix;
import com.fastcash.moneytransfer.dto.APIResponse;
import com.fastcash.moneytransfer.dto.AccountRequest;
import com.fastcash.moneytransfer.dto.UserResponse;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.service.AccountService;
import com.fastcash.moneytransfer.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Validated
@ApiBaseUrlPrefix
@RequestMapping("${endpoint.account}")
@Tag(name = "Account Controller", description = "Account Controller API")
public class AccountController {
	
	private final AccountService accountService;
	private final UserService userService;
	private final ReloadableResourceBundleMessageSource messageSource;
	
	public AccountController(AccountService accountService, UserService userService, ReloadableResourceBundleMessageSource messageSource) {
		this.accountService = accountService;
		this.userService = userService;
		this.messageSource = messageSource;
	}
	
	@Operation(summary = "update account", parameters = { @Parameter(in = ParameterIn.PATH, name = "id", description = "Account Id") })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Update account", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class)) }),
	})
	@SecurityRequirement(name = "Bearer Key")
	@PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)	
	public ResponseEntity<APIResponse> update(@Valid @PathVariable Long id, @Valid @RequestBody AccountRequest accountRequest) {	
        UserAccount userAccount = accountService.findById(id).get();
        userAccount.setAllowOverdraft(accountRequest.allowOverdraft());
        User user = accountService.update(userAccount, userService);
        
        APIResponse apiResponse = new APIResponse(
        	messageSource.getMessage("AcountUpdate", null, LocaleContextHolder.getLocale()), 
    		null, 
    		null,
    		new UserResponse(user)
    	);
        
        return ResponseEntity.ok(apiResponse);
	}
	
}
