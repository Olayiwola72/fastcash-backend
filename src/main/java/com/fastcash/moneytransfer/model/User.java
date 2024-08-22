package com.fastcash.moneytransfer.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fastcash.moneytransfer.enums.AuthMethod;
import com.fastcash.moneytransfer.enums.UserRoles;
import com.fastcash.moneytransfer.enums.UserType;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

@Entity
@DiscriminatorValue("USER")
public class User extends BaseUser {
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<UserAccount> userAccounts;
	
	@OneToMany(mappedBy = "debitedUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<MoneyTransfer> moneyTransfers;
	
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<AccountStatement> accountStatements;
	
	private String familyName;
	
    private String givenName;
    
	private String pictureUrl;
	
    private boolean emailVerified;
    
    private boolean defaultPassword;
    
    private String externalUserId;
	
    private Date lastLoginDate;
    
	public User() {
		super();
		this.setAccounts(new ArrayList<>());
		this.setTransfers(new ArrayList<>());
		this.setAccountStatements(new ArrayList<>());
	}
	
	public User(String email, String password) {
		this();
		this.setEmail(email);
		this.setPassword(password);
		this.setUserType(UserType.INTERNAL);
		this.setAuthMethod(AuthMethod.LOCAL);
		this.setRoles(UserRoles.USER.toString());
	}
	
	public User(String email, String password, String familyName, String givenName, String name, String pictureUrl, boolean emailVerified, String externalUserId) {
		this(email, password);
		this.setFamilyName(familyName);
		this.setGivenName(givenName);
		this.setName(name);
		this.setPictureUrl(pictureUrl);
		this.setEmailVerified(emailVerified);
		this.setExternalUserId(externalUserId);
		this.setUserType(UserType.EXTERNAL);
		this.setAuthMethod(AuthMethod.GOOGLE);
		this.setDefaultPassword(true);
	}
	
	public List<UserAccount> getAccounts() {
		return userAccounts;
	}

	public void setAccounts(List<UserAccount> userAccounts) {
		this.userAccounts = userAccounts;
	}	

	public List<MoneyTransfer> getTransfers() {
		return moneyTransfers;
	}

	public void setTransfers(List<MoneyTransfer> moneyTransfers) {
		this.moneyTransfers = moneyTransfers;
	}
	
	public List<AccountStatement> getAccountStatements() {
		return accountStatements;
	}

	public void setAccountStatements(List<AccountStatement> accountStatements) {
		this.accountStatements = accountStatements;
	}
	
	public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    
	public boolean isDefaultPassword() {
		return defaultPassword;
	}

	public void setDefaultPassword(boolean defaultPassword) {
		this.defaultPassword = defaultPassword;
	}
    
    public String getExternalUserId() {
		return externalUserId;
	}

	public void setExternalUserId(String externalUserId) {
		this.externalUserId = externalUserId;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

}
