package com.fastcash.moneytransfer.service;

import java.io.IOException;

import com.fastcash.moneytransfer.model.FailedNotification;
import com.fastcash.moneytransfer.model.NotificationContext;

import jakarta.mail.MessagingException;

public interface EmailNotifiable {
	void sendUserCreationNotification(NotificationContext notificationContext);
	void sendUserLoginNotification(NotificationContext notificationContext);
    void sendUserUpdateNotification(NotificationContext notificationContext);
    void sendUserPasswordResetNotification(NotificationContext notificationContext);
    void sendUserPasswordChangeNotification(NotificationContext notificationContext);
    void sendUserDeletionNotification(NotificationContext notificationContext);
    void sendUserAccountUpdateNotification(NotificationContext notificationContext);
    void sendUserAccountTransferNotification(NotificationContext notificationContext);
	void retryNotification(FailedNotification failedNotification) throws MessagingException, IOException;
	void saveFailedNotification(NotificationContext notificationContext, String subject, String template, String reason);
}
