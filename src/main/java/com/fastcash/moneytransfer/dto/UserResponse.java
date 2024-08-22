package com.fastcash.moneytransfer.dto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.fastcash.moneytransfer.enums.AuthMethod;
import com.fastcash.moneytransfer.enums.UserType;
import com.fastcash.moneytransfer.model.AccountStatement;
import com.fastcash.moneytransfer.model.Admin;
import com.fastcash.moneytransfer.model.BaseUser;
import com.fastcash.moneytransfer.model.InternalAccount;
import com.fastcash.moneytransfer.model.InternalChargeAccount;
import com.fastcash.moneytransfer.model.MoneyTransfer;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "User Response Information")
public class UserResponse {
	
	@NotNull
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "id", example = "1")
	private Long id;
	
	@NotNull
    @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "email", example = "test@moneytransfer.com")
    private String email;
	
	@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "name", example = "John Doe")
    private String name;
	
	@NotNull
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "is user enabled?", example = "true")
    private boolean enabled;
	
	@NotEmpty
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "user role", example = "USER")
    private String roles;
	
	@NotNull
    @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "user provider")
    private AuthMethod authMethod;
	
	@NotNull
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "creation date and time", example = "2022-04-25T10:15:30")
    private LocalDateTime createdAt;
    
    @NotNull
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "user type", example = "INTERNAL")
    private UserType userType;
	
	private int version;
	
	@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "user accounts")
	private List<UserAccount> userAccounts;
	
	// Method for obtaining sorted transfers in descending order
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "user transfers in descending order")
	private List<MoneyTransferResponse> moneyTransfers;
	
    // Method for obtaining sorted statements in descending order
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "user statements in descending order")
	private List<AccountStatementResponse> accountStatements;
	
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "family name", example = "John")
    private String familyName;
    
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "given name", example = "Doe")
    private String givenName;
    
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "picture url", example = "http://picture.url.jpg")
    private String pictureUrl;
    
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "is email verified", example = "true")
    private boolean emailVerified;
    
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "is default password", example = "true")
    private boolean defaultPassword;
    
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "external user id", example = "111222333")
    private String externalUserId;
    
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "last login date and time", example = "2022-04-25T10:15:30")
    private Date lastLoginDate;
    
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "admin accounts")
	private List<InternalAccount> internalAccounts;
	
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "admin charge accounts")
    private List<InternalChargeAccount> chargeAccounts;
    
    public UserResponse (BaseUser baseUser) {
    	this.id = baseUser.getId();
    	this.email = baseUser.getEmail();
    	this.name = baseUser.getName();
    	this.enabled = baseUser.isEnabled();
    	this.roles = baseUser.getRoles();
    	this.authMethod = baseUser.getAuthMethod();
    	this.createdAt = baseUser.getCreatedAt();
    	this.userType = baseUser.getUserType();
    	this.version = baseUser.getVersion();
    	
    	if(baseUser instanceof User) {
    		User user = (User) baseUser;
    		
    		this.userAccounts = user.getAccounts();
    		this.familyName = user.getFamilyName();
    		this.givenName = user.getGivenName();
    		this.pictureUrl = user.getPictureUrl();
    		this.emailVerified = user.isEmailVerified();
    		this.defaultPassword = user.isDefaultPassword();
    		this.externalUserId = user.getExternalUserId();
    		this.lastLoginDate = user.getLastLoginDate();
    		this.moneyTransfers = sortTransfersByCreationDateDescending(user.getTransfers());
    		this.accountStatements = sortAccountStatementsByCreationDateDescending(user.getAccountStatements());
    	}else {
    		Admin user = (Admin) baseUser;
    		
    		this.internalAccounts = user.getInternalAccounts();
    		this.chargeAccounts = user.getChargeAccounts();
    		this.accountStatements = sortAccountStatementsByCreationDateDescending(user.getAccountStatements());
    	}
    }
	
    public Long getId() {
		return id;
	}
    
    public String getEmail() {
		return email;
	}
    
    public String getName() {
        return name;
    }
    
    public boolean isEnabled() {
		return enabled;
	}
    
    public String getRoles() {
		return roles;
	}
    
    public AuthMethod getAuthMethod() {
		return authMethod;
	}
    
    public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public UserType getUserType() {
		return userType;
	}
	
	public int getVersion() {
		return version;
	}
	
	public List<UserAccount> getAccounts() {
		return userAccounts;
	}

	public List<MoneyTransferResponse> getTransfers() {
		return moneyTransfers;
	}
	
	public List<AccountStatementResponse> getAccountStatements() {
		return accountStatements;
	}
	
	public String getFamilyName() {
        return familyName;
    }
	
    public String getGivenName() {
        return givenName;
    }
	
    public String getPictureUrl() {
        return pictureUrl;
    }
    
    public boolean isEmailVerified() {
        return emailVerified;
    }
    
    public boolean isDefaultPassword() {
        return defaultPassword;
    }
	
    public String getExternalUserId() {
		return externalUserId;
	}
    
    public Date getLastLoginDate() {
		return lastLoginDate;
	}
    
    public List<InternalAccount> getInternalAccounts() {
		return internalAccounts;
	}

	public List<InternalChargeAccount> getChargeAccounts() {
		return chargeAccounts;
	}

    private List<MoneyTransferResponse> sortTransfersByCreationDateDescending(List<MoneyTransfer> transfers) {
        return transfers.stream()
        		.map(transfer -> new MoneyTransferResponse(transfer))
                .sorted(Comparator.comparing(MoneyTransferResponse::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
    
    private List<AccountStatementResponse> sortAccountStatementsByCreationDateDescending(List<AccountStatement> accountStatements) {
        return accountStatements.stream()
        		.map(accountStatement -> new AccountStatementResponse(accountStatement))
                .sorted(Comparator.comparing(AccountStatementResponse::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
    
}

