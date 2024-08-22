package com.fastcash.moneytransfer.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.repository.UserRepository;
import com.fastcash.moneytransfer.validation.UserTypeChecker;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class InternalExternalUserFilter extends BasicAuthenticationFilter {

	private final HandlerExceptionResolver resolver;
	private final UserRepository userRepository;
	private final UserTypeChecker userTypeChecker;
	private BadCredentialsException badCredentialsException;

	public InternalExternalUserFilter(AuthenticationManager authenticationManager,
			@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
			ReloadableResourceBundleMessageSource messageSource, 
			UserRepository userRepository,
			UserTypeChecker userTypeChecker) {
		super(authenticationManager);
		this.resolver = resolver;
		this.userRepository = userRepository;
		this.userTypeChecker = userTypeChecker;
		this.badCredentialsException = new BadCredentialsException(
			messageSource.getMessage("username.password.incorrect", null, LocaleContextHolder.getLocale())
		);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {

		String header = request.getHeader("Authorization");

		if (StringUtils.hasText(header) && header.startsWith("Basic ")) {
			try {

				String base64Credentials = header.substring(6);
				String decodedCredentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));
				String[] credentials = decodedCredentials.split(":", 2);

				if (credentials.length == 2) {
					String username = credentials[0];
					String password = credentials[1];

					// Check if username or password is empty
					if (!StringUtils.hasLength(username) || !StringUtils.hasLength(password)) {
						handleException(request, response, badCredentialsException);
					}

					Optional<User> optionalUser = userRepository.findByEmailAndDeletedIsFalse(username);
					if(optionalUser.isPresent()) {
						User user = optionalUser.get();
						
						if(!user.getUserType().isInternal()) {
							resolver.resolveException(request, response, null, userTypeChecker.handleUserNotInternal(user));
							return;
						}
					}
				} else {
					handleException(request, response, badCredentialsException);
				}

			} catch (Exception e) {}
		}

		// Proceed with the filter chain if no exceptions were thrown
		filterChain.doFilter(request, response);
	}

	private void handleException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
		resolver.resolveException(request, response, null, exception);
		return;
	}
}
