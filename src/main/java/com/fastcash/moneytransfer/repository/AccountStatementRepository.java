package com.fastcash.moneytransfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fastcash.moneytransfer.model.AccountStatement;

@Repository
public interface AccountStatementRepository extends JpaRepository<AccountStatement, Long> {
	
}
