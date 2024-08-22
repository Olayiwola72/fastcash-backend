package com.fastcash.moneytransfer.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserRolesTest {

	@Test
    void testTransactionTypeSizeNotEqualToZero() {
        assertNotEquals(0, UserRoles.values().length);
    }

}
