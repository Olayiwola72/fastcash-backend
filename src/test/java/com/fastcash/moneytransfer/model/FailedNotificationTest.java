package com.fastcash.moneytransfer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.enums.NotificationType;

class FailedNotificationTest {
	
	private NotificationContext notificationContext;
	private final Long id = 0L;
	private final String subject = "subject";
	private final String template = "template";
	private final String reason = "reason";
	private User user;
	
	@BeforeEach
	void setUp() {
		user = new User();
		user.setEmail("test@email.com");
		
		notificationContext = new NotificationContext(NotificationType.EMAIL, user);
	}
	
	@Test
	void testFailedNotificationDefaultInitialization() {
		FailedNotification failedNotification = new FailedNotification();
		
		assertNull(failedNotification.getId()); // Id should be null
		assertNull(failedNotification.getNotificationContext()); // notificationContext should be null
		assertNull(failedNotification.getSubject()); // subject should be null
		assertNull(failedNotification.getTemplate()); // template should be null
		assertNull(failedNotification.getFailureReason()); // reason should be null
		assertEquals(0, failedNotification.getRetryCount());
	}
	
	@Test
	void testFailedNotificationCustomInitialization() {
		FailedNotification failedNotification = new FailedNotification(notificationContext, subject, template, reason);
		
		assertNull(failedNotification.getId()); // Id should be null
		assertEquals(notificationContext, failedNotification.getNotificationContext()); // notificationContext not should be null
		assertEquals(subject, failedNotification.getSubject()); // subject should not be null
		assertEquals(template, failedNotification.getTemplate()); // template should not be null
		assertEquals(reason, failedNotification.getFailureReason()); // reason should not be null
		assertEquals(0, failedNotification.getRetryCount());
	}
	
	@Test
	void testGettersAndSetters() {
		FailedNotification failedNotification = new FailedNotification();
		failedNotification.setId(id);
		failedNotification.setNotificationContext(notificationContext);
		failedNotification.setSubject(subject);
		failedNotification.setTemplate(template);
		failedNotification.setFailureReason(reason);
		failedNotification.setRetryCount(1);
		
		assertEquals(id, failedNotification.getId()); // Id should not be null
		assertEquals(notificationContext, failedNotification.getNotificationContext()); // notificationContext not should be null
		assertEquals(subject, failedNotification.getSubject()); // subject should not be null
		assertEquals(template, failedNotification.getTemplate()); // template should not be null
		assertEquals(reason, failedNotification.getFailureReason()); // reason should not be null
		assertEquals(1, failedNotification.getRetryCount()); // retryCount should not be 1
	}
	
	@Test
	void testIncrementRetryCount() {
		FailedNotification failedNotification = new FailedNotification();
		failedNotification.incrementRetryCount();
		
		assertEquals(1, failedNotification.getRetryCount());
	}

}
