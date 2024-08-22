package com.fastcash.moneytransfer.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fastcash.moneytransfer.enums.NotificationType;
import com.fastcash.moneytransfer.model.FailedNotification;
import com.fastcash.moneytransfer.model.NotificationContext;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.repository.FailedNotificationRepository;

import jakarta.mail.MessagingException;

class FailedNotificationRetryServiceTest {

    @Mock
    private FailedNotificationRepository failedNotificationRepository;

    @Mock
    private EmailNotifiable emailNotifiable;

    @InjectMocks
    private FailedNotificationRetryService failedNotificationRetryService;
    
    @Mock
    private User user;
    
    @Mock
    private NotificationContext notificationContext;
    
    @Mock
    private FailedNotification failedNotification;
    
    private final String subject = "subject";
    private final String template = "template";
    private final String reason = "reason";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mocking user, notificationContext, and failedNotification
        user = mock(User.class);
        when(user.getEmail()).thenReturn("test@email.com");

        notificationContext = mock(NotificationContext.class);
        when(notificationContext.getNotificationType()).thenReturn(NotificationType.EMAIL);
        when(notificationContext.getUser()).thenReturn(user);
        
        failedNotification = mock(FailedNotification.class);
        when(failedNotification.getNotificationContext()).thenReturn(notificationContext);
        when(failedNotification.getSubject()).thenReturn(subject);
        when(failedNotification.getTemplate()).thenReturn(template);
        when(failedNotification.getFailureReason()).thenReturn(reason);
    }

    @Test
    void retryFailedNotifications_successfulRetry() throws Exception {
        // Arrange
        when(failedNotificationRepository.findAll()).thenReturn(Arrays.asList(failedNotification));

        // Act
        failedNotificationRetryService.retryFailedNotifications();

        // Assert
        verify(emailNotifiable).retryNotification(failedNotification);
        verify(failedNotificationRepository).delete(failedNotification);
    }

    @Test
    void retryFailedNotifications_retryFails() throws Exception {
        // Arrange
        when(failedNotificationRepository.findAll()).thenReturn(Arrays.asList(failedNotification));
        doThrow(new MessagingException("Test error message")).when(emailNotifiable).retryNotification(failedNotification);

        // Act
        failedNotificationRetryService.retryFailedNotifications();

        // Assert
        verify(failedNotificationRepository, never()).delete(failedNotification);
        verify(failedNotification).incrementRetryCount();
        verify(failedNotification).setLastAttemptDate(any(Date.class));
        verify(failedNotification).setFailureReason("Test error message");
        verify(failedNotificationRepository).save(failedNotification);
    }
    
    @Test
    void retryFailedNotifications_failureReasonExceeds255Characters() throws Exception {
        // Arrange
        String longFailureReason = "a".repeat(300); // Generate a string with 300 'a' characters
        when(failedNotificationRepository.findAll()).thenReturn(Arrays.asList(failedNotification));
        doThrow(new MessagingException(longFailureReason)).when(emailNotifiable).retryNotification(failedNotification);

        // Act
        failedNotificationRetryService.retryFailedNotifications();

        // Assert
        verify(failedNotification).incrementRetryCount();
        verify(failedNotification).setLastAttemptDate(any(Date.class));
        verify(failedNotification).setFailureReason(longFailureReason.substring(0, 255)); // Only the first 255 characters should be saved
        verify(failedNotificationRepository).save(failedNotification);
    }
    
    @Test
    void retryFailedNotifications_failureReasonLessThanOrEqual255Characters() throws Exception {
        // Arrange
        String shortFailureReason = "This is a short error message";
        when(failedNotificationRepository.findAll()).thenReturn(Arrays.asList(failedNotification));
        doThrow(new MessagingException(shortFailureReason)).when(emailNotifiable).retryNotification(failedNotification);

        // Act
        failedNotificationRetryService.retryFailedNotifications();

        // Assert
        verify(failedNotification).incrementRetryCount();
        verify(failedNotification).setLastAttemptDate(any(Date.class));
        verify(failedNotification).setFailureReason(shortFailureReason); // The full short message should be saved
        verify(failedNotificationRepository).save(failedNotification);
    }
}
