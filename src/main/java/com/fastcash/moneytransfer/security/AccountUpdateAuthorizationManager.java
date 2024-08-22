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
public class AccountUpdateAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
	
	private static final UriTemplate USER_URI_TEMPLATE = new UriTemplate("/account/{accountId}");
	private final UserService userService;
	
	public AccountUpdateAuthorizationManager(UserService userService) {
		this.userService = userService;
	}
	
	@Override
	public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
		// Extract the accountId from the request URI: /account/{accountId}
		Map<String, String> uriVariables = USER_URI_TEMPLATE.match(context.getRequest().getRequestURI());
		String accountIdFromReqeuestUri = uriVariables.get("accountId");
		
		// Extract the email from the Authentication object, which is a Jwt object
		String emailFromJwt = authenticationSupplier.get().getName();
		
		// Retrieve the user from the database using its email
		Optional<User> optionalUser = userService.findByEmail(emailFromJwt);
		if(optionalUser.isEmpty()) return new AuthorizationDecision(false);
		
		// Check if the user has role "ROLE_USER"
		boolean hasUserRole = userService.isUser(authenticationSupplier.get());

		// Check if the user has role "ROLE_ADMIN"
		boolean hasAdminRole = userService.isAdmin(authenticationSupplier.get());
		
		// Check if the accountId belongs to the currently logged in user		
		boolean accountIdMatch = optionalUser.get().getAccounts().stream().anyMatch(account -> account.getId().toString().equals(accountIdFromReqeuestUri));
		
		return new AuthorizationDecision(hasAdminRole || (accountIdMatch && hasUserRole));
	}

}
