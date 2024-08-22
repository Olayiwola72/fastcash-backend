package com.fastcash.moneytransfer.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fastcash.moneytransfer.dto.GoogleUserResponse;
import com.fastcash.moneytransfer.dto.UserRequestMapper;
import com.fastcash.moneytransfer.exception.InvalidIDTokenException;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class GoogleAuthService {

	private final UserService userService;
	private final UserRepository userRepository;
	private final ReloadableResourceBundleMessageSource messageSource;
	private final UserRequestMapper userRequestMapper;
	protected GoogleIdTokenVerifier verifier;

	public GoogleAuthService(@Value("${oauth.client.id}") String clientId, UserService userService, UserRepository userRepository,
			ReloadableResourceBundleMessageSource messageSource, UserRequestMapper userRequestMapper) {
		this.userService = userService;
		this.userRepository = userRepository;
		this.messageSource = messageSource;
		this.userRequestMapper = userRequestMapper;
		
		HttpTransport transport = new NetHttpTransport();
		JsonFactory jsonFactory = new GsonFactory();
		this.verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
				.setAudience(Collections.singletonList(clientId)).build();
	}
	
	public GoogleUserResponse getUser(String idTokenString) throws GeneralSecurityException, IOException {
		GoogleIdToken idToken = verifyToken(idTokenString);

		if (idToken != null) {
			GoogleIdToken.Payload payload = idToken.getPayload();
			String email = payload.getEmail();
			
			User existingUser = userService.isUserPresent(email);
			User googleUser = userRequestMapper.toGoogleUser(existingUser, payload);
			
			if(existingUser == null) {
				googleUser = userService.create(googleUser);
			}else {
				googleUser = userRepository.save(googleUser);
			}
			
			return new GoogleUserResponse(existingUser, googleUser);
		} else {
			throw new InvalidIDTokenException(
				messageSource.getMessage("TokenInvalid", null, LocaleContextHolder.getLocale())
			);
		}
	}

	protected GoogleIdToken verifyToken(String idTokenString) throws GeneralSecurityException, IOException {
		return verifier.verify(idTokenString);
	}
}
