package com.fastcash.moneytransfer.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fastcash.moneytransfer.enums.NotificationType;
import com.fastcash.moneytransfer.model.FailedNotification;
import com.fastcash.moneytransfer.model.NotificationContext;
import com.fastcash.moneytransfer.model.User;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class FailedNotificationRepositoryTest {

    @Autowired
    private FailedNotificationRepository failedNotificationRepository;
    
    @Autowired
    private NotificationContextRepository notificationContextRepository;

    @Autowired
    private UserRepository userRepository; // Assuming you have a UserRepository
    
    private NotificationContext notificationContext1;
    private NotificationContext notificationContext2;
    private User user;
    
	private final String subject = "subject";
	private final String template = "template";
	private final String reason = "reason";
    private final String email = "john.doe@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize your user object here
        user = new User(email, "password");
        user.setRoles("USER");
        userRepository.save(user);
        
        notificationContext1 = new NotificationContext(NotificationType.EMAIL, user);
        notificationContextRepository.save(notificationContext1);
        
        notificationContext2 = new NotificationContext(NotificationType.EMAIL, user);
        notificationContextRepository.save(notificationContext2);
    }

    @Test
    void testDeleteAllByUser() {
        // Create failed notifications associated with the user
        FailedNotification notification1 = new FailedNotification(notificationContext1, subject, template, reason);
        failedNotificationRepository.save(notification1);

        FailedNotification notification2 = new FailedNotification(notificationContext2, subject, template, reason);
        failedNotificationRepository.save(notification2);

        // Verify that the notifications are saved
        List<FailedNotification> notifications = failedNotificationRepository.findAll();
        assertEquals(2, notifications.size());

        // Delete all notifications by user
        failedNotificationRepository.deleteAllByUser(user);

        // Verify that all notifications for the user are deleted
        notifications = failedNotificationRepository.findAll();
        assertTrue(notifications.isEmpty());
    }
}
