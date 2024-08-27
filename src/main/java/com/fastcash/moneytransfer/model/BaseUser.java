package com.fastcash.moneytransfer.model;

import java.time.LocalDateTime;
import java.util.Locale;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.context.i18n.LocaleContextHolder;

import com.fastcash.moneytransfer.enums.AuthMethod;
import com.fastcash.moneytransfer.enums.UserType;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "users", uniqueConstraints = {
	@UniqueConstraint(name = "unique_email", columnNames = "email")
})
public class BaseUser {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Email(message = "Please provide a valid email address")
    @NotEmpty(message = "Email is required.")
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;
	
	@NotEmpty(message = "Password is required.")
	@Column(name = "password", nullable = false, length = 60) // BCrypt hashed passwords are 60 characters long
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	
	@Column(name = "enabled")
	private boolean enabled;
	
	@NotEmpty(message = "Roles are required.")
	@Column(length = 50)
	private String roles; // space seperated string
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AuthMethod authMethod;
	
    private String name;
    
	@NotNull
	@CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @NotNull
    private UserType userType;
    
    @Column(nullable = false)
    private Locale preferredLanguage;
    
	@Version
	@Column(name = "version", nullable = false)
	private int version;
	
    private boolean deleted = Boolean.FALSE;
	
	public BaseUser() {
		this.setPreferredLanguage(LocaleContextHolder.getLocale());
		this.setEnabled(true);
		this.setCreatedAt(LocalDateTime.now());
	}
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * @return the username
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param username the username to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the roles
	 */
	public String getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(String roles) {
		this.roles = roles;
	}

	public AuthMethod getAuthMethod() {
		return authMethod;
	}

	public void setAuthMethod(AuthMethod authMethod) {
		this.authMethod = authMethod;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	
	public Locale getPreferredLanguage() {
		return preferredLanguage;
	}

	public void setPreferredLanguage(Locale preferredLanguage) {
		this.preferredLanguage = preferredLanguage;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
