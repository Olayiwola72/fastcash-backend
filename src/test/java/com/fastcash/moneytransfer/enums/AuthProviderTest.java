package com.fastcash.moneytransfer.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AuthProviderTest {
	
	@Test
    void testAuthProvideSizeNotEqualToZero() {
        assertNotEquals(0, AuthMethod.values().length);
    }

}
