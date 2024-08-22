package com.fastcash.moneytransfer.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastcash.moneytransfer.annotation.ApiBaseUrlPrefix;
import com.fastcash.moneytransfer.dto.APIResponse;
import com.fastcash.moneytransfer.dto.GoogleCredentialsRequest;
import com.fastcash.moneytransfer.dto.GoogleUserResponse;
import com.fastcash.moneytransfer.dto.PasswordUpdateRequest;
import com.fastcash.moneytransfer.dto.UserRequest;
import com.fastcash.moneytransfer.dto.UserRequestMapper;
import com.fastcash.moneytransfer.dto.UserResponse;
import com.fastcash.moneytransfer.dto.UserUpdateRequest;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.service.GoogleAuthService;
import com.fastcash.moneytransfer.service.RefreshTokenService;
import com.fastcash.moneytransfer.service.TokenAuthenticationService;
import com.fastcash.moneytransfer.service.UserService;
import com.fastcash.moneytransfer.validation.ExistingUsernameValidator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@Validated
@ApiBaseUrlPrefix
@RequestMapping("${endpoint.user}")
@Tag(name = "User Controller", description = "User Controller API")
public class UserController {

	private final UserService userService;
	private final TokenAuthenticationService tokenAuthenticationService;
	private final UserRequestMapper userRequestMapper;
	private final ExistingUsernameValidator existingUsernameValidator;
	private final GoogleAuthService googleAuthService;
	private final ReloadableResourceBundleMessageSource messageSource;
	private final RefreshTokenService refreshTokenService;
	
	public UserController(
			UserService userService, 
			TokenAuthenticationService tokenAuthenticationService,
			UserRequestMapper userRequestMapper, 
			ExistingUsernameValidator existingUsernameValidator,
			GoogleAuthService googleAuthService,
			ReloadableResourceBundleMessageSource messageSource,
			RefreshTokenService refreshTokenService
	) {
		this.userService = userService;
		this.tokenAuthenticationService = tokenAuthenticationService;
		this.userRequestMapper = userRequestMapper;
		this.existingUsernameValidator = existingUsernameValidator;
		this.googleAuthService = googleAuthService;
		this.messageSource = messageSource;
		this.refreshTokenService = refreshTokenService;
	}
	
	@Operation(summary = "Get User By Id", parameters = { @Parameter(in = ParameterIn.PATH, name = "id", description = "User Id") })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Get User By Id", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)) }),
	})
	@SecurityRequirement(name = "Basic Auth")
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)	
	public ResponseEntity<UserResponse> get(@PathVariable(required = true) Long id) {	
        User user = userService.findById(id).get();
        UserResponse userResponse = new UserResponse(user);
        return ResponseEntity.ok(userResponse);
	}
	
	@Operation(summary = "Create User")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "User created and signed in successfully", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class)) }),
	})
	@SecurityRequirement(name = "Basic Auth")
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<APIResponse> create(
		@Valid @RequestBody UserRequest userRequest,
		@RequestHeader(value = "User-Agent", defaultValue = "Unknown Device") String userAgent
	) {
		existingUsernameValidator.isEmailExisting(userRequest.email());
		
		User user = userRequestMapper.toUser(userRequest);
        user = userService.create(user);
        
        // Automatically log in the user
        String token = tokenAuthenticationService.authenticateUser(user.getEmail(), userRequest.password());
        
        APIResponse apiResponse = new APIResponse(
        	messageSource.getMessage("UserCreation", null, LocaleContextHolder.getLocale()), 
    		token, 
    		refreshTokenService.createRefreshToken(user, userAgent).getToken(),
    		new UserResponse(user)
    	);
        
        return ResponseEntity.ok(apiResponse);
	}
	
	@Operation(summary = "Continue with Google")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "User created and signed in successfully", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class)) }),
	})
	@SecurityRequirement(name = "Basic Auth")
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping(path = "/google", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<APIResponse> create(
		@Valid @RequestBody GoogleCredentialsRequest googleCredentialsRequest, 
		HttpServletRequest request,
		@RequestHeader(value = "User-Agent", defaultValue = "Unknown Device") String userAgent
	) throws GeneralSecurityException, IOException {	
		GoogleUserResponse googleUserResponse = googleAuthService.getUser(googleCredentialsRequest.credential());
		User user = googleUserResponse.googleUser();
		
        // Automatically log in the user
        Authentication authentication = tokenAuthenticationService.authenticateUser(user.getEmail(), request);
        String token = tokenAuthenticationService.generateToken(authentication, googleUserResponse.existingUser());
        
        APIResponse apiResponse = new APIResponse(
        	messageSource.getMessage("UserCreation", null, LocaleContextHolder.getLocale()), 
    		token, 
    		refreshTokenService.findByUserAndUserAgent(user, userAgent).getToken(),
    		new UserResponse(user)
    	);
            
        return ResponseEntity.ok(apiResponse);
	}
	
	@Operation(summary = "Update user", parameters = { @Parameter(in = ParameterIn.PATH, name = "id", description = "User Id") })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "User updated successfully", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class)) }),
	})
	@SecurityRequirement(name = "Bearer Key")
	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<APIResponse> update(@PathVariable(required = true) Long id, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
		User user = userService.findById(id).get();
		user = userRequestMapper.toUpdateUser(user, userUpdateRequest);
		userService.update(user);
		
		APIResponse apiResponse = new APIResponse(
        	messageSource.getMessage("UserUpdate", null, LocaleContextHolder.getLocale()), 
    		null, 
    		null,
    		new UserResponse(user)
    	);
            
        return ResponseEntity.ok(apiResponse);
	}
	
	@Operation(summary = "Change password", parameters = { @Parameter(in = ParameterIn.PATH, name = "id", description = "User Id") })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Password updated successfully", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class)) }),
	})
	@SecurityRequirement(name = "Bearer Key")
	@PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<APIResponse> update(@PathVariable(required = true) @NotNull Long id, @Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest) {	
		User user = userService.findById(id).get();
		user = userRequestMapper.toUpdateUserPassword(user, passwordUpdateRequest);
		userService.updatePassword(user);
		
		APIResponse apiResponse = new APIResponse(
        	messageSource.getMessage("UserPasswordUpdate", null, LocaleContextHolder.getLocale()), 
    		null, 
    		null,
    		null
    	);
            
        return ResponseEntity.ok(apiResponse);
	}
	
	@Operation(summary = "Delete User", parameters = { @Parameter(in = ParameterIn.PATH, name = "id", description = "User Id") })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Delete User", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class)) }),
	})
	@SecurityRequirement(name = "Bearer Key")
	@DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)	
	public ResponseEntity<APIResponse> delete(@PathVariable(required = true) Long id) {
		userService.softDeleteUserById(id);
		
		APIResponse apiResponse = new APIResponse(
        	messageSource.getMessage("UserDeleted", null, LocaleContextHolder.getLocale()), 
    		null, 
    		null,
    		null
    	);
            
        return ResponseEntity.ok(apiResponse);
	}
	
}
