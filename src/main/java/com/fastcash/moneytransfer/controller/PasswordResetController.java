package com.fastcash.moneytransfer.controller;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastcash.moneytransfer.annotation.ApiBaseUrlPrefix;
import com.fastcash.moneytransfer.dto.APIResponse;
import com.fastcash.moneytransfer.dto.ForgotPasswordRequest;
import com.fastcash.moneytransfer.dto.ResetPasswordRequest;
import com.fastcash.moneytransfer.service.PasswordResetTokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@Validated
@ApiBaseUrlPrefix
@RequestMapping("${endpoint.password}")
public class PasswordResetController {

    private final PasswordResetTokenService passwordResetTokenService;
    private final ReloadableResourceBundleMessageSource messageSource;
    
    public PasswordResetController(PasswordResetTokenService passwordResetTokenService, ReloadableResourceBundleMessageSource messageSource){
    	this.passwordResetTokenService = passwordResetTokenService;
    	this.messageSource = messageSource;
    }

    @Operation(summary = "Forgot Password")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Password reset link sent to your email.", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class)) }),
	})
    @PostMapping("${endpoint.password.forgot}")
    public ResponseEntity<APIResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetTokenService.createPasswordResetToken(request.email());

        APIResponse apiResponse = new APIResponse(
        	messageSource.getMessage("PasswordResetSent", null, LocaleContextHolder.getLocale()), 
    		null, 
    		null,
    		null
        );
            
        return ResponseEntity.ok(apiResponse);
    }
    
    @Operation(summary = "Reset Password")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Password reset successful.", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class)) }),
	})
    @PostMapping(path = "${endpoint.password.reset}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetTokenService.resetPassword(request.token(), request.password());
        
        APIResponse apiResponse = new APIResponse(
        	messageSource.getMessage("PasswordResetSuccess", null, LocaleContextHolder.getLocale()), 
    		null, 
    		null,
    		null
    	);
        
        return ResponseEntity.ok(apiResponse);
    }
    
}