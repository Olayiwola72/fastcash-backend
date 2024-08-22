package com.fastcash.moneytransfer.enums;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UserTypeTest {

    @Test
    public void testIsInternal() {
        // Test the isInternal method for INTERNAL user type
        assertTrue(UserType.INTERNAL.isInternal(), "INTERNAL should be internal");

        // Test the isInternal method for EXTERNAL user type
        assertFalse(UserType.EXTERNAL.isInternal(), "EXTERNAL should not be internal");

        // Test the isInternal method for LINKED user type
        assertTrue(UserType.LINKED.isInternal(), "LINKED should be internal");
    }

    @Test
    public void testEnumValues() {
        // Verify the enum values
        UserType[] expectedValues = { UserType.INTERNAL, UserType.EXTERNAL, UserType.LINKED };
        assertArrayEquals(expectedValues, UserType.values(), "Enum values should match");
    }

    @Test
    public void testEnumValueOf() {
        // Verify the valueOf method
        assertEquals(UserType.INTERNAL, UserType.valueOf("INTERNAL"), "ValueOf INTERNAL should return INTERNAL");
        assertEquals(UserType.EXTERNAL, UserType.valueOf("EXTERNAL"), "ValueOf EXTERNAL should return EXTERNAL");
        assertEquals(UserType.LINKED, UserType.valueOf("LINKED"), "ValueOf LINKED should return LINKED");
    }
}
