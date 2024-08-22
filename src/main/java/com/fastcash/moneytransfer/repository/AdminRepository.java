package com.fastcash.moneytransfer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fastcash.moneytransfer.model.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
	
	// Derived Query Method to find a User by their email
	Optional<Admin> findByEmail(String email);

}
