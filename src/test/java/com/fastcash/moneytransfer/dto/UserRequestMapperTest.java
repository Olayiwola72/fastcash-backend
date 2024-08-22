package com.fastcash.moneytransfer.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fastcash.moneytransfer.enums.AuthMethod;
import com.fastcash.moneytransfer.enums.UserType;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.service.PasswordService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

class UserRequestMapperTest {

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private UserRequestMapper userRequestMapper;
    
    private final String email = "test@example.com";
    private final String password = "password123";
    private final String familyName = "Doe";
    private final String givenName = "John";
    private final String name = "John Doe";
    private final String pictureUrl = "http://example.com/picture.jpg";
    private final boolean emailVerified = true;
    private final String userId = "123456789";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testToUser() {
        UserRequest userRequest = new UserRequest(email, password);
        User user = userRequestMapper.toUser(userRequest);

        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(AuthMethod.LOCAL, user.getAuthMethod());
        assertEquals(UserType.INTERNAL, user.getUserType());
    }

    @Test
    void testToGoogleUser_NewUser() {
        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);

        when(payload.getEmail()).thenReturn(email);
        when(payload.get("family_name")).thenReturn(familyName);
        when(payload.get("given_name")).thenReturn(givenName);
        when(payload.get("name")).thenReturn(name);
        when(payload.get("picture")).thenReturn(pictureUrl);
        when(payload.getEmailVerified()).thenReturn(emailVerified);
        when(payload.getSubject()).thenReturn(userId);

        when(passwordService.generateStrongPassword()).thenReturn(password);

        User user = userRequestMapper.toGoogleUser(null, payload);

        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(familyName, user.getFamilyName());
        assertEquals(givenName, user.getGivenName());
        assertEquals(name, user.getName());
        assertEquals(pictureUrl, user.getPictureUrl());
        assertEquals(emailVerified, user.isEmailVerified());
        assertEquals(userId, user.getExternalUserId());
        assertEquals(AuthMethod.GOOGLE, user.getAuthMethod());
        assertEquals(UserType.EXTERNAL, user.getUserType());
    }

    @Test
    void testToGoogleUser_ExistingUser() {
        User existingUser = new User();
        existingUser.setEmail(email);
        
        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);

        when(payload.getEmail()).thenReturn(email);
        when(payload.get("family_name")).thenReturn(familyName);
        when(payload.get("given_name")).thenReturn(givenName);
        when(payload.get("name")).thenReturn(name);
        when(payload.get("picture")).thenReturn(pictureUrl);
        when(payload.getEmailVerified()).thenReturn(emailVerified);
        when(payload.getSubject()).thenReturn(userId);

        when(passwordService.generateStrongPassword()).thenReturn(password);

        User user = userRequestMapper.toGoogleUser(existingUser, payload);

        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertNull(user.getPassword()); // assert that password did not change, its null in the first place
        assertEquals(familyName, user.getFamilyName());
        assertEquals(givenName, user.getGivenName());
        assertEquals(name, user.getName());
        assertEquals(pictureUrl, user.getPictureUrl());
        assertEquals(emailVerified, user.isEmailVerified());
        assertEquals(userId, user.getExternalUserId());
        assertEquals(AuthMethod.GOOGLE, user.getAuthMethod());
    }
    
    @Test
    void testToUpdateUser() {
    	String name = "John Doe";
    	UserUpdateRequest userUpdateRequest = new UserUpdateRequest(name);
    	User user = new User();
        User updateUser = userRequestMapper.toUpdateUser(user, userUpdateRequest);

        assertEquals(name, updateUser.getName());
    }
    
    @Test
    void testToUpdateUserPassword() {
    	String password = "123456";
    	PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest(password);
    	User user = new User();
        User updateUser = userRequestMapper.toUpdateUserPassword(user, passwordUpdateRequest);

        assertEquals(password, updateUser.getPassword());
    }
    
    @Test
    void testToDisableUser() {
    	User user = new User();
    	user.setEnabled(true);
        User updateUser = userRequestMapper.toDisableUser(user);

        assertFalse(updateUser.isEnabled());
    }
}
