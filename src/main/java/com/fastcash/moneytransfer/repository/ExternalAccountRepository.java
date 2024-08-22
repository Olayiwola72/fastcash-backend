package com.fastcash.moneytransfer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fastcash.moneytransfer.model.ExternalAccount;

@Repository
public interface ExternalAccountRepository extends JpaRepository<ExternalAccount, Long> {
	
	// Derived Query Method to find a User by their email
	Optional<ExternalAccount> findByAccountNumber(Long accountNumber);
}
