package com.fastcash.moneytransfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fastcash.moneytransfer.model.InternalChargeAccount;

@Repository
public interface InternalChargeAccountRepository extends JpaRepository<InternalChargeAccount, Long> {

}
