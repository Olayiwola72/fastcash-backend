package com.fastcash.moneytransfer.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.hibernate.Hibernate;
import org.hibernate.ObjectNotFoundException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastcash.moneytransfer.enums.NotificationType;
import com.fastcash.moneytransfer.enums.UserType;
import com.fastcash.moneytransfer.model.Admin;
import com.fastcash.moneytransfer.model.BaseUser;
import com.fastcash.moneytransfer.model.NotificationContext;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserPrincipal;
import com.fastcash.moneytransfer.repository.AdminRepository;
import com.fastcash.moneytransfer.repository.BaseUserRepository;
import com.fastcash.moneytransfer.repository.FailedNotificationRepository;
import com.fastcash.moneytransfer.repository.RefreshTokenRepository;
import com.fastcash.moneytransfer.repository.UserRepository;
import com.fastcash.moneytransfer.validation.UserValidator;

@Service
@Transactional
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;
	private final AdminRepository adminRepository;
	private final BaseUserRepository baseUserRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final FailedNotificationRepository failedNotificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final AccountService accountService;
    private final InternalAccountService internalAccountService;
	private final InternalChargeAccountService internalChargeAccountService;
	private final EmailNotifiable emailNotifiable;

	public UserService(
			UserRepository userRepository, 
			AdminRepository adminRepository,
			BaseUserRepository baseUserRepository,
			RefreshTokenRepository refreshTokenRepository,
			FailedNotificationRepository failedNotificationRepository,
			PasswordEncoder passwordEncoder, 
			UserValidator userValidator,
			AccountService accountService,
			InternalAccountService internalAccountService,
			InternalChargeAccountService internalChargeAccountService,
			EmailNotifiable emailNotifiable
		) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.baseUserRepository = baseUserRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.failedNotificationRepository = failedNotificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.userValidator = userValidator;
        this.accountService = accountService;
        this.internalAccountService = internalAccountService;
		this.internalChargeAccountService = internalChargeAccountService;
		this.emailNotifiable = emailNotifiable;
    }
	
	public List<User> findAll(){
		return this.userRepository.findAll();
	}
	
	public Optional<User> findById(Long userId){
		User user = this.userRepository.findById(userId)
				.orElseThrow(() -> new ObjectNotFoundException("user", userId));
		
		if(user.isDeleted()) throw new AccessDeniedException("Access Denied");
		
		// Fetch accounts if they are not fetched already
		initializeUserCollections(user);
	    
	    return Optional.of(user);
	}
	
	public Optional<User> findByEmail(String email){
		User user = userRepository.findByEmailAndDeletedIsFalse(email)
                .orElseThrow(userValidator::handleUserNotFound);

		initializeUserCollections(user);
		
		return Optional.of(user);
	}
	
	public Optional<Admin> findAdminByEmail(String email) {
		Admin admin = adminRepository.findByEmail(email)
		        .orElseThrow(userValidator::handleUserNotFound);

	    return Optional.of(admin);
	}

	public User create(User user) {
        // Encode the password and save the user
		user = (User) saveBaseUser(user);

        // Create account and save to user object in memory
        user.setAccounts(accountService.create(user));
        
        // Fetch accounts if they are not fetched already
        initializeUserCollections(user);
        
        // Send deletion notification email asynchronously
        emailNotifiable.sendUserCreationNotification(new NotificationContext(NotificationType.EMAIL, user));
        return user;
    }

    public Admin create(Admin admin) {
        // Encode the password and save the admin
        saveBaseUser(admin);

        // Perform any additional operations for Admin
        internalAccountService.create(admin);
        internalChargeAccountService.create(admin);
        return admin;
    }

	public User update(User user) {
	    userRepository.save(user);
	    
	    // Send deletion notification email asynchronously
	    emailNotifiable.sendUserUpdateNotification(new NotificationContext(NotificationType.EMAIL, user));
        return user;
	}
	
	public User updatePassword(User user) {
		user.setPassword(user.getPassword());
		if(user.isDefaultPassword() && user.getUserType().equals(UserType.EXTERNAL)) {
			user.setDefaultPassword(false);
			user.setUserType(UserType.LINKED);
		}
		// Encode the password and save the user
		user = (User) saveBaseUser(user);
		
		// Send deletion notification email asynchronously
		emailNotifiable.sendUserPasswordChangeNotification(new NotificationContext(NotificationType.EMAIL, user));
        
        return user;
	}
	
	public void deleteById(Long userId){
		Optional<User> optionalUser = findById(userId);
		
		if(optionalUser.isPresent()) {
			userRepository.deleteById(userId);
		}		
	}
	
	public User softDeleteUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setDeleted(true); // soft delete user
            user = userRepository.save(user);
            
            asyncSoftDeleteUserById(user);
            
            return user;
        }else {
        	return null;
        }
    }
	
	@Async
	protected CompletableFuture<Void> asyncSoftDeleteUserById(User user) {
		// Send deletion notification email asynchronously
        emailNotifiable.sendUserDeletionNotification(new NotificationContext(NotificationType.EMAIL, user));  
        
		// Soft delete associated entities
        user.getAccounts().forEach(account -> account.setDeleted(true));
        user.getTransfers().forEach(transfer -> transfer.setDeleted(true));
        user.getAccountStatements().forEach(statement -> statement.setDeleted(true));
      
        user = userRepository.save(user);
        
        refreshTokenRepository.deleteAllByUser(user);
        failedNotificationRepository.deleteAllByUser(user);
        return CompletableFuture.completedFuture(null);
    }
	
	public User isUserPresent(String email) {
		Optional<User> optionalUser = userRepository.findByEmailAndDeletedIsFalse(email);
		
		if(optionalUser.isPresent()) {
			// Fetch collections if they are not fetched already
			initializeUserCollections(optionalUser.get());
			return optionalUser.get();
		}else {
			return null;
		}
	}
	
	public User findUserByAccountId(Long accountId) {
		Optional<User> optionalUser = userRepository.findByUserAccounts_Id(accountId);
        
        if(optionalUser.isPresent()) {
			// Fetch collections if they are not fetched already
			initializeUserCollections(optionalUser.get());
			return optionalUser.get();
		}else {
			return null;
		}
    }
	
	@Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return baseUserRepository.findByEmailAndDeletedIsFalse(email) // First we need to find the user from database.
        		.map(user -> new UserPrincipal(user)) // If found wrap the returned user instance in a UserPrincipal instance.
                .orElseThrow(() -> userValidator.handleUserNotFound());//Otherwise, return UsernameNotFoundException 
    }
	
	public void updateLastLoginDate(String email) {
        Optional<User> optionalUser = userRepository.findByEmailAndDeletedIsFalse(email);
        
        if(optionalUser.isPresent()) {
        	User user = optionalUser.get();
            user.setLastLoginDate(new Date());
            userRepository.save(user);
        }
    }
    
	// Method to check if the current user is an admin
	public boolean isAdmin(Authentication authentication) {
	    return authentication.getAuthorities().stream()
	    	.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
	}
	
	// Method to check if the current user is a user
	public boolean isUser(Authentication authentication) {
	    return authentication.getAuthorities().stream()
	    	.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));
	}
	
	private <T extends BaseUser> BaseUser saveBaseUser(T baseUser) {
        // Encode the password
        baseUser.setPassword(passwordEncoder.encode(baseUser.getPassword()));

        // Save the base user to the appropriate repository
        if (baseUser instanceof User) {
            userRepository.save((User) baseUser);
        } else if (baseUser instanceof Admin) {
            adminRepository.save((Admin) baseUser);
        }
        
		return baseUser;
    }
	
	private void initializeUserCollections(User user) {
        Hibernate.initialize(user.getAccounts());
        Hibernate.initialize(user.getTransfers());
        Hibernate.initialize(user.getAccountStatements());
    }
	
}
