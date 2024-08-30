package com.fastcash.moneytransfer.controller;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastcash.moneytransfer.annotation.ApiBaseUrlPrefix;
import com.fastcash.moneytransfer.dto.APIResponse;
import com.fastcash.moneytransfer.dto.TokenResponse;
import com.fastcash.moneytransfer.dto.UserResponse;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.service.RefreshTokenService;
import com.fastcash.moneytransfer.service.TokenAuthenticationService;
import com.fastcash.moneytransfer.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@ApiBaseUrlPrefix
@RequestMapping("${app.api.auth-path}")
@Tag(name = "Auth Controller", description = "Auth Controller API")
public class AuthController {
	
	private final TokenAuthenticationService tokenAuthenticationService;
	private final UserService userService;
	private final ReloadableResourceBundleMessageSource messageSource;
	private final RefreshTokenService refreshTokenService;

	public AuthController(
			TokenAuthenticationService tokenAuthenticationService,
			UserService userService,
			ReloadableResourceBundleMessageSource messageSource,
			RefreshTokenService refreshTokenService
		) {
		this.tokenAuthenticationService = tokenAuthenticationService;
		this.userService = userService;
		this.messageSource = messageSource;
		this.refreshTokenService = refreshTokenService;
	}
	
	@Operation(summary = "Get Authorization Token")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Response token generated successfully", content = {
			@Content(schema = @Schema(implementation = TokenResponse.class)) }),
	})
	@SecurityRequirement(name = "Basic Auth")
	@PreAuthorize("hasRole('USER')")
	@PostMapping(path = "${app.api.token-path}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TokenResponse> token(
		Authentication authentication,
		@RequestHeader(value = "User-Agent", defaultValue = "Unknown Device") String userAgent
	) {	
		User user = userService.findByEmail(authentication.getName()).get();
		String token = tokenAuthenticationService.generateToken(authentication, null);
		String refreshToken = refreshTokenService.findByUserAndUserAgent(user, userAgent).getToken();
		return ResponseEntity.ok(new TokenResponse(token, refreshToken));
	}
	
	@Operation(summary = "Sign in User")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "User signed in successfully", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class)) }),
	})
	@SecurityRequirement(name = "Bearer Key")
	@PostMapping(path = "${app.api.login-path}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<APIResponse> login(
		Authentication authentication,
		@RequestHeader(value = "User-Agent", defaultValue = "Unknown Device") String userAgent
	) {	
		User user = userService.findByEmail(authentication.getName()).get();

		String token = tokenAuthenticationService.generateToken(authentication, user);
        
        APIResponse apiResponse = new APIResponse(
        	messageSource.getMessage("UserCreation", null, LocaleContextHolder.getLocale()), 
    		token, 
    		refreshTokenService.findByUserAndUserAgent(user, userAgent).getToken(),
    		new UserResponse(user)
    	);
        
        return ResponseEntity.ok(apiResponse);
	}
	
}
