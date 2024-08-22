package com.fastcash.moneytransfer.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fastcash.moneytransfer.model.User;

class UserAvatarDtoTest {
	
	private final String pictureUrl = "https://example.com/picture.jpg";
	private final String name = "John Doe";
	private final String email = "john.doe@example.com";

    @Test
    void testAvatarUrlFromPictureUrl() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getPictureUrl()).thenReturn(pictureUrl);

        UserAvatarDto dto = new UserAvatarDto(user);

        assertEquals(pictureUrl, dto.getAvatarURL());
    }

    @Test
    void testAvatarUrlFromName() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getPictureUrl()).thenReturn(null);
        Mockito.when(user.getName()).thenReturn(name);

        UserAvatarDto dto = new UserAvatarDto(user);

        assertEquals("https://ui-avatars.com/api/?name="+name+"&background=random", dto.getAvatarURL());
    }

    @Test
    void testAvatarUrlFromEmail() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getPictureUrl()).thenReturn(null);
        Mockito.when(user.getName()).thenReturn(null);
        Mockito.when(user.getEmail()).thenReturn(email);

        UserAvatarDto dto = new UserAvatarDto(user);

        assertEquals("https://ui-avatars.com/api/?name="+email+"&background=random", dto.getAvatarURL());
    }
}
