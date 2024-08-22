package com.fastcash.moneytransfer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fastcash.moneytransfer.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	// Derived Query Method to find a User by their email and its not deleted
	Optional<User> findByEmailAndDeletedIsFalse(String email);
	
	// Derived Query Method to find a User by an UserAccount ID
    Optional<User> findByUserAccounts_Id(Long accountId);
    
}
