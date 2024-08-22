package com.fastcash.moneytransfer.model;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

public class UserPrincipal implements UserDetails {
	
	private static final long serialVersionUID = 1L;
	
	private BaseUser user;
	
	public UserPrincipal(BaseUser user) {
		this.setUser(user);
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Convert a user's roles from space-delimited string to a lost of SimpleGrantedAuthority objects
		// E.g. john's roles are stored ina string like "admin user moderator", we need to convert it to a list of GrantedAuthority
		// Before coversion, we need to add this "ROLE_" prefix to
		return Arrays.stream(StringUtils.tokenizeToStringArray(this.user.getRoles(), " "))
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.toList();
	}

	@Override
	public String getPassword() {
		return this.user.getPassword();
	}

	@Override
	public String getUsername() {
		return this.user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.user.isEnabled();
	}

	/**
	 * @return the user
	 */
	public BaseUser getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(BaseUser user) {
		this.user = user;
	}

}
