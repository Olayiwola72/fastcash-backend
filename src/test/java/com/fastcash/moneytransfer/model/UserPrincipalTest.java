package com.fastcash.moneytransfer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

class UserPrincipalTest {
	
	@Test
    void testDefaultConstructor() {
		testGetUser();
	}
	
	@Test
    void testGetAuthorities() {
        // Test when user has multiple roles
        User userWithRoles = new User();
        userWithRoles.setRoles("ADMIN USER MODERATOR");

        UserPrincipal userPrincipal = new UserPrincipal(userWithRoles);

        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();
        List<String> authorityStrings = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        assertTrue(authorityStrings.contains("ROLE_ADMIN"));
        assertTrue(authorityStrings.contains("ROLE_USER"));
        assertTrue(authorityStrings.contains("ROLE_MODERATOR"));
    }

    @Test
    void testGetPassword() {
        // Test getPassword method
        User user = new User();
        user.setPassword("password123");

        UserPrincipal userPrincipal = new UserPrincipal(user);

        assertEquals("password123", userPrincipal.getPassword());
    }

    @Test
    void testGetUsername() {
        // Test getUsername method
        User user = new User();
        user.setEmail("john_doe");

        UserPrincipal userPrincipal = new UserPrincipal(user);

        assertEquals("john_doe", userPrincipal.getUsername());
    }
    
    @Test
    void testIsAccountNonExpired() {
        // Test isAccountNonExpired method
        User user = new User();
        UserPrincipal userPrincipal = new UserPrincipal(user);

        assertTrue(userPrincipal.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        // Test isAccountNonLocked method
        User user = new User();
        UserPrincipal userPrincipal = new UserPrincipal(user);

        assertTrue(userPrincipal.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        // Test isCredentialsNonExpired method
        User user = new User();
        UserPrincipal userPrincipal = new UserPrincipal(user);

        assertTrue(userPrincipal.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        // Test isEnabled method
        User user = new User();
        user.setEnabled(true);

        UserPrincipal userPrincipal = new UserPrincipal(user);

        assertTrue(userPrincipal.isEnabled());
    }

    @Test
    void testGetUser() {
        // Test getUser/setUser method
        User user = new User();
        UserPrincipal userPrincipal = new UserPrincipal(user);

        User newUser = new User();
        userPrincipal.setUser(newUser);


        assertEquals(newUser, userPrincipal.getUser());
    }
}
