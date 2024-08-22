package com.fastcash.moneytransfer.util;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fastcash.moneytransfer.model.Admin;
import com.fastcash.moneytransfer.repository.AdminRepository;
import com.fastcash.moneytransfer.service.UserService;

@Component
public class DBAdminUserInitializer {
	private final UserService userService;
	private final AdminRepository adminRepository;
	
	public DBAdminUserInitializer(
			@Value("${app.admin.email}") String email,
			@Value("${app.admin.password}") String password,
			@Value("${app.admin.user.roles}") String roles,
			@Value("${spring.application.name}") String applicationName,
			UserService userService,
			AdminRepository adminRepository
			) {
		this.userService = userService;
		this.adminRepository = adminRepository;
		createAdminUser(email, password, roles, applicationName);
	}
	
	private void createAdminUser(String email, String password, String roles, String applicationName) {
		Optional<Admin> optionalAdmin = adminRepository.findByEmail(email);

		if(optionalAdmin.isEmpty()) {
			Admin admin = new Admin();
			admin.setEmail(email);
			admin.setPassword(password);
			admin.setRoles(roles);
			admin.setName(applicationName);
			
			admin = userService.create(admin);
		}
	}
	
}
