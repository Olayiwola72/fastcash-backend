package com.fastcash.moneytransfer.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastcash.moneytransfer.annotation.ApiBaseUrlPrefix;
import com.fastcash.moneytransfer.dto.TokenRefreshRequest;
import com.fastcash.moneytransfer.dto.TokenResponse;
import com.fastcash.moneytransfer.model.RefreshToken;
import com.fastcash.moneytransfer.service.RefreshTokenService;
import com.fastcash.moneytransfer.service.TokenAuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@Validated
@ApiBaseUrlPrefix
@RequestMapping("${endpoint.auth}")
@Tag(name = "RefreshTokenController", description = "Refresh Controller API")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;
    private final TokenAuthenticationService tokenAuthenticationService;
    
    public RefreshTokenController(RefreshTokenService refreshTokenService, TokenAuthenticationService tokenAuthenticationService) {
    	this.refreshTokenService = refreshTokenService;
    	this.tokenAuthenticationService = tokenAuthenticationService;
    }
    
    @Operation(summary = "Refresh Token")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Refresh token generated successfully", content = {
			@Content(schema = @Schema(implementation = TokenResponse.class)) }),
	})
	@PostMapping(path = "${endpoint.token.refresh}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> refreshtoken(@Valid @RequestBody TokenRefreshRequest request, HttpServletRequest httpServletRequest) {
        String requestRefreshToken = request.refreshToken();

        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken);
        refreshToken = refreshTokenService.verifyExpiration(refreshToken);

        Authentication authentication = tokenAuthenticationService.authenticateUser(refreshToken.getUser().getEmail(), httpServletRequest);
        String token = tokenAuthenticationService.generateToken(authentication, null);

        return ResponseEntity.ok(new TokenResponse(token, requestRefreshToken));
    }
    
}

