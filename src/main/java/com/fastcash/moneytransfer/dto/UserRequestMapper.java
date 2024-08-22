package com.fastcash.moneytransfer.dto;

import org.springframework.stereotype.Component;

import com.fastcash.moneytransfer.enums.AuthMethod;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.service.PasswordService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

@Component
public class UserRequestMapper {
	private final PasswordService passwordService;
	
	public UserRequestMapper(PasswordService passwordService){
		this.passwordService = passwordService;
	}
	
	public User toUser(UserRequest userRequest) {
		User user = new User(userRequest.email(), userRequest.password());
        return user;
	}
	
	public User toGoogleUser(User user, GoogleIdToken.Payload payload) {
		String email = payload.getEmail();
		String familyName = (String) payload.get("family_name");
		String givenName = (String) payload.get("given_name");
		String name = (String) payload.get("name");
		String pictureUrl = (String) payload.get("picture");
		boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
		String userId = payload.getSubject();
		
		if (user != null) {
			user.setFamilyName(familyName);
			user.setGivenName(givenName);
			user.setName(name);
			user.setPictureUrl(pictureUrl);
			user.setEmailVerified(emailVerified);
			user.setExternalUserId(userId);
			user.setAuthMethod(AuthMethod.GOOGLE);
		} else {
			user = new User(email, passwordService.generateStrongPassword(), familyName, givenName, name, pictureUrl, emailVerified, userId);
		}

		return user;
	}
	
	public User toUpdateUser(User user, UserUpdateRequest userUpdateRequest) {
		user.setName(userUpdateRequest.name());
		return user;
	}
	
	public User toUpdateUserPassword(User user, PasswordUpdateRequest passwordUpdateRequest) {
		user.setPassword(passwordUpdateRequest.password());		
		return user;
	}
	
	public User toDisableUser(User user) {
		user.setEnabled(false);		
		return user;
	}
}