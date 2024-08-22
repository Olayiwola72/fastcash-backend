package com.fastcash.moneytransfer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fastcash.moneytransfer.model.BaseUser;

@Repository
public interface BaseUserRepository extends JpaRepository<BaseUser, Long> {
	
	// Derived Query Method to find a BaseUser by their email and its not deleted
	Optional<BaseUser> findByEmailAndDeletedIsFalse(String email);
	
}
