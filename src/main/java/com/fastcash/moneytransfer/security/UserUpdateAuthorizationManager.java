package com.fastcash.moneytransfer.security;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.service.UserService;

@Component
public class UserUpdateAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
	
	private static final UriTemplate USER_URI_TEMPLATE = new UriTemplate("/user/{userId}");
	private final UserService userService;
	
	public UserUpdateAuthorizationManager(UserService userService) {
		this.userService = userService;
	}
	
	@Override
	public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
		// Extract the userId from the request URI: /user/{userId}
		Map<String, String> uriVariables = USER_URI_TEMPLATE.match(context.getRequest().getRequestURI());
		String userIdFromReqeuestUri = uriVariables.get("userId");
		
		// Extract the email from the Authentication object, which is a Jwt object
		String emailFromJwt = authenticationSupplier.get().getName();
		
		// Retrieve the user from the database using its email
		Optional<User> optionalUser = userService.findByEmail(emailFromJwt);
		if(optionalUser.isEmpty()) return new AuthorizationDecision(false);
		String userIdFromRepository = optionalUser.get().getId().toString();
		
		// Check if the user has role "ROLE_USER"
		boolean hasUserRole = userService.isUser(authenticationSupplier.get());

		// Check if the user has role "ROLE_ADMIN"
		boolean hasAdminRole = userService.isAdmin(authenticationSupplier.get());
		
		// Check if the accountId belongs to the currently logged in user		
		boolean userIdMatch = userIdFromRepository.equals(userIdFromReqeuestUri);
				
		return new AuthorizationDecision(hasAdminRole || (userIdMatch && hasUserRole));
	}

}
