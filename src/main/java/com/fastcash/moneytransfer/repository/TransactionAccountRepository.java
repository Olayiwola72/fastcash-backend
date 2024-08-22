package com.fastcash.moneytransfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fastcash.moneytransfer.model.TransactionAccount;

@Repository
public interface TransactionAccountRepository extends JpaRepository<TransactionAccount, Long> {
	
}
