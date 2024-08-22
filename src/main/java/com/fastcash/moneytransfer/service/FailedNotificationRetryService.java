package com.fastcash.moneytransfer.service;

import java.util.Date;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fastcash.moneytransfer.model.FailedNotification;
import com.fastcash.moneytransfer.repository.FailedNotificationRepository;

@Service
public class FailedNotificationRetryService {

    private final FailedNotificationRepository failedNotificationRepository;
    private final EmailNotifiable emailNotifiable;
    
    public FailedNotificationRetryService(
    	FailedNotificationRepository failedNotificationRepository, 
    	EmailNotifiable emailNotifiable
    ) {
    	this.failedNotificationRepository = failedNotificationRepository;
    	this.emailNotifiable = emailNotifiable;
    }
    
    @Scheduled(fixedRateString = "${retry.interval}", initialDelayString = "${retry.initialDelay}")
    public void retryFailedNotifications() {
        List<FailedNotification> failedNotifications = failedNotificationRepository.findAll();
       
        for (FailedNotification failedNotification : failedNotifications) {
            try {
            	emailNotifiable.retryNotification(failedNotification);
                failedNotificationRepository.delete(failedNotification);
            } catch (Exception e) {
            	String fullFailureReason = e.getMessage();
            	
                failedNotification.incrementRetryCount();
                failedNotification.setLastAttemptDate(new Date());
                failedNotification.setFailureReason(fullFailureReason.length() > 255 ? fullFailureReason.substring(0, 255) : fullFailureReason);
                
                failedNotificationRepository.save(failedNotification);
            }
        }
    }
}
