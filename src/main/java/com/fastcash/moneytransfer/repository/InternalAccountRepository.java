package com.fastcash.moneytransfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fastcash.moneytransfer.model.InternalAccount;

@Repository
public interface InternalAccountRepository extends JpaRepository<InternalAccount, Long> {

}
