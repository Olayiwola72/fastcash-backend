package com.fastcash.moneytransfer.enums;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class NotificationTypeTest {
	
	@Test
    void testNotificationTypeSizeNotEqualToZero() {
        assertNotEquals(0, NotificationType.values().length);
    }

}
