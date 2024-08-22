package com.fastcash.moneytransfer.dto;

import com.fastcash.moneytransfer.model.User;

public record GoogleUserResponse(User existingUser, User googleUser) {

}
