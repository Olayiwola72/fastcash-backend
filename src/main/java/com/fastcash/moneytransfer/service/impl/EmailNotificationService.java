package com.fastcash.moneytransfer.service.impl;

import java.io.IOException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.fastcash.moneytransfer.config.ApiProperties;
import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.enums.NotificationType;
import com.fastcash.moneytransfer.model.FailedNotification;
import com.fastcash.moneytransfer.model.NotificationContext;
import com.fastcash.moneytransfer.repository.FailedNotificationRepository;
import com.fastcash.moneytransfer.repository.NotificationContextRepository;
import com.fastcash.moneytransfer.repository.UserRepository;
import com.fastcash.moneytransfer.service.EmailNotifiable;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@Primary
public class EmailNotificationService implements EmailNotifiable {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine; // Spring Boot's template engine
    private final FailedNotificationRepository failedNotificationRepository;
    private final NotificationContextRepository notificationContextRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final ApiProperties apiProperties;
    private final String companyName;
    private final String companyPage;
    private final String logoUrl;
    
    public EmailNotificationService(
            JavaMailSender javaMailSender,
            TemplateEngine templateEngine,
            FailedNotificationRepository failedNotificationRepository,
            NotificationContextRepository notificationContextRepository,
            UserRepository userRepository,
            MessageSource messageSource,
            @Value("${app.companyName}") String companyName,
            @Value("${app.companyPage}") String companyPage,
            @Value("${app.logoUrl}") String logoUrl,
            ApiProperties apiProperties
            
        ) {
            this.javaMailSender = javaMailSender;
            this.templateEngine = templateEngine;
            this.failedNotificationRepository = failedNotificationRepository;
            this.notificationContextRepository = notificationContextRepository;
            this.userRepository = userRepository;
            this.messageSource = messageSource;
            this.apiProperties = apiProperties;
            this.companyName = companyName;
            this.companyPage = companyPage;
            this.logoUrl = logoUrl;
        }
    
    @Async
    @Override
    public void sendUserCreationNotification(NotificationContext notificationContext) {
        String subject = messageSource.getMessage("user.creation.subject", new Object[]{companyName}, getLocale(notificationContext));
        sendNotification(notificationContext, subject, "create_user_email");
    }
    
    @Async
    @Override
    public void sendUserLoginNotification(NotificationContext notificationContext) {
        String subject = messageSource.getMessage("user.login.subject", new Object[]{companyName}, getLocale(notificationContext));
        sendNotification(notificationContext, subject, "login_user_email");
    }
    
    @Async
    @Override
    public void sendUserUpdateNotification(NotificationContext notificationContext) {
        String subject = messageSource.getMessage("user.update.subject", null, getLocale(notificationContext));
        sendNotification(notificationContext, subject, "update_user_email");
    }
    
    @Async
    @Override
    public void sendUserPasswordResetNotification(NotificationContext notificationContext) {
        String subject = messageSource.getMessage("user.password.reset.subject", null, getLocale(notificationContext));
        sendNotification(notificationContext, subject, "password_reset_user_email");
    }
    
    @Async
    @Override
    public void sendUserPasswordChangeNotification(NotificationContext notificationContext) {
        String subject = messageSource.getMessage("user.password.change.subject", null, getLocale(notificationContext));
        sendNotification(notificationContext, subject, "password_change_user_email");
    }

    @Async
    @Override
    public void sendUserDeletionNotification(NotificationContext notificationContext) {
        String subject = messageSource.getMessage("user.deletion.subject", null, getLocale(notificationContext));
        sendNotification(notificationContext, subject, "delete_user_email");
    }
    
    @Async
    @Override
    public void sendUserAccountUpdateNotification(NotificationContext notificationContext) {
        String subject = messageSource.getMessage("user.account.update.subject", null, getLocale(notificationContext));
        sendNotification(notificationContext, subject, "update_account_email");
    }
    
    @Override
    @Async
    public void sendUserAccountTransferNotification(NotificationContext notificationContext) {
        String subject = messageSource.getMessage(
            "user.account.transfer.subject",
            new Object[]{companyName, notificationContext.getTransactionDirection(), notificationContext.getTransactionAmount()},
            getLocale(notificationContext)
        );
        sendNotification(notificationContext, subject, "transaction_user");
    }

    private void sendNotification(NotificationContext notificationContext, String subject, String template) {
        try {
            String htmlContent = generateHtmlContent(notificationContext, template);
            send(notificationContext.getUser().getEmail(), subject, htmlContent);
        } catch (Exception e) {
            saveFailedNotification(notificationContext, subject, template, e.getMessage());
        }
    }
    
    private void send(String userEmail, String subject, String htmlContent) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

        messageHelper.setText(htmlContent, true);
        messageHelper.setTo(userEmail);
        messageHelper.setSubject(subject);

        javaMailSender.send(mimeMessage);
    }
    
    private String generateHtmlContent(NotificationContext notificationContext, String template) throws IOException {
    	Locale locale = getLocale(notificationContext); // Get user's preferred locale

        Context context = new Context(locale);  // Pass the locale to the context
        
        context.setVariable("userName", notificationContext.getName()); // Set the variable for Thymeleaf template
        context.setVariable("accounts", notificationContext.getUser().getAccounts()); // Set the variable for Thymeleaf template
        context.setVariable("companyName", companyName); // Pass companyName to template
        context.setVariable("companyPage", companyPage); // Pass companyPage to template
        context.setVariable("logoUrl", logoUrl); // Pass logoUrl to template
        context.setVariable("lastLoginDate", notificationContext.getLastLoginDate()); // Pass lastLoginDate to template
        context.setVariable("loginUrl", companyPage + "/login"); // Pass resetUrl to template
        
        context.setVariable("accountId", notificationContext.getAccountId()); // Pass accountId to template
        context.setVariable("accountBalance", notificationContext.getAccountBalance()); // Pass accountBalance to template
        
        context.setVariable("transactionDirection", notificationContext.getTransactionDirection()); // Pass transactionDirection to template
        context.setVariable("transactionType", notificationContext.getTransactionType()); // Pass transactionType to template
        context.setVariable("transactionAmount", notificationContext.getTransactionAmount()); // Pass transactionAmount to template
        context.setVariable("transactionDate", notificationContext.getTransactionDate()); // Pass transactionDate to template
        context.setVariable("transactionNotes", notificationContext.getTransactionNotes()); // Pass transactionNotes to template
        context.setVariable("transactionId", notificationContext.getTransactionId()); // Pass transactionId to template
        context.setVariable("transactionChargeAmount", notificationContext.getTransactionChargeAmount()); // Pass transactionChargeAmount to template
        context.setVariable("transactionExternalBankName", notificationContext.getTransactionExternalBankName()); // Pass transactionExternalBankName to template
        context.setVariable("transactionExternalAccountName", notificationContext.getTransactionExternalAccountName()); // Pass transactionExternalAccountName to template
        context.setVariable("transactionExternalAccountIdAndCurrency", notificationContext.getTransactionExternalAccountIdAndCurrency()); // Pass transactionExternalAccountIdAndCurrency to template
        
        context.setVariable("passwordRestTokenExpiryHours", Constants.PASSWORD_RESET_TOKEN_EXPIRY_HOURS); // Pass passwordRestTokenExpiryHours to template
        context.setVariable("resetUrl", apiProperties.resetPasswordUrlPath() + "?token=" + notificationContext.getToken()); // Pass resetUrl to template
        
        return templateEngine.process(template, context);
    }
    
    @Override
    @Transactional
    public void saveFailedNotification(NotificationContext notificationContext, String subject, String content, String reason) {
        // Flush the persistence context to ensure all entities are fully persisted
        userRepository.flush();
        
        notificationContextRepository.save(notificationContext);
        
        FailedNotification failedNotification = new FailedNotification(notificationContext, subject, content, reason);
        failedNotificationRepository.save(failedNotification);
    }

    @Override
    public void retryNotification(FailedNotification failedNotification) throws MessagingException, IOException {
        NotificationContext notificationContext = failedNotification.getNotificationContext();
        
        if (NotificationType.EMAIL.equals(notificationContext.getNotificationType())) {
            send(
                notificationContext.getUser().getEmail(),
                failedNotification.getSubject(),
                generateHtmlContent(notificationContext, failedNotification.getTemplate())
            );
        }
    }

    private Locale getLocale(NotificationContext notificationContext) {
        // Adjust this to get the locale from the user or other context
        return notificationContext.getUser().getPreferredLanguage();
    }
}
