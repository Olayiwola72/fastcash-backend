package com.fastcash.moneytransfer.service;

import java.io.IOException;

import com.fastcash.moneytransfer.model.FailedNotification;
import com.fastcash.moneytransfer.model.NotificationContext;

import jakarta.mail.MessagingException;

public interface SMSNotifiable {
    void sendUserAccountUpdateNotification(NotificationContext notificationContext);
    void sendUserAccountTransferNotification(NotificationContext notificationContext);
    void saveFailedNotification(NotificationContext notificationContext, String subject, String template, String reason);
    void retryNotification(FailedNotification failedNotification) throws MessagingException, IOException;
}
