package com.fastcash.moneytransfer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fastcash.moneytransfer.model.PasswordResetToken;
import com.fastcash.moneytransfer.model.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    
    void deleteAllByUser(User user);
}
