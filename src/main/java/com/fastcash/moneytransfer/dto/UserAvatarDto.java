package com.fastcash.moneytransfer.dto;

import com.fastcash.moneytransfer.model.User;

public class UserAvatarDto {
    private final String avatarURL;

    public UserAvatarDto(User user) {
        if (user.getPictureUrl() != null) {
            this.avatarURL = user.getPictureUrl();
        } else {
            String nameOrEmail = user.getName() != null ? user.getName() : user.getEmail();
            this.avatarURL = generateAvatarURLFromName(nameOrEmail);
        }
    }

    private String generateAvatarURLFromName(String name) {
        return String.format("https://ui-avatars.com/api/?name=%s&background=random", name);
    }

    public String getAvatarURL() {
        return avatarURL;
    }
}
