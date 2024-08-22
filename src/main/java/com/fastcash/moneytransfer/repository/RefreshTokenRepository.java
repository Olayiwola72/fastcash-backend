package com.fastcash.moneytransfer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fastcash.moneytransfer.model.RefreshToken;
import com.fastcash.moneytransfer.model.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserAndUserAgent(User user, String userAgent);
    void deleteByUserAndUserAgent(User user, String userAgent);
    void deleteAllByUser(User user);
    
}