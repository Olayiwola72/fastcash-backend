package com.fastcash.moneytransfer.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.NotificationType;
import com.fastcash.moneytransfer.enums.TransactionDirection;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fastcash.moneytransfer.model.AccountStatement;
import com.fastcash.moneytransfer.model.FailedNotification;
import com.fastcash.moneytransfer.model.NotificationContext;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.repository.FailedNotificationRepository;
import com.fastcash.moneytransfer.repository.NotificationContextRepository;
import com.fastcash.moneytransfer.repository.UserRepository;

import jakarta.mail.internet.MimeMessage;

class EmailNotificationServiceTest {

    @Mock
    private JavaMailSender mockMailSender;

    @Mock
    private TemplateEngine mockTemplateEngine;

    @InjectMocks
    private EmailNotificationService emailNotificationService;
    
    @Mock
    private FailedNotificationRepository mockFailedNotificationRepository;
    
    @Mock
    private NotificationContextRepository notificationContextRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private MessageSource messageSource;

    private User user;
	private UserAccount userAccount;
    private AccountStatement accountStatement;
    private NotificationContext notificationContext;
    
	private final String subject = "subject";
	private final String template = "template";
	private final String reason = "reason";
    private final NotificationType notificationType = NotificationType.EMAIL;
    private final String htmlContent = "<html><body>Mock HTML Content</body></html>";
    
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		
		user = new User();
		user.setEmail("test@email.com");
		user.setName("John Date");
		user.setLastLoginDate(new Date());
		
		userAccount = new UserAccount(Currency.USD, user);
		userAccount.setId(1L);
        userAccount.setBalance(new BigDecimal("5000.00"));
        
        accountStatement = new AccountStatement();
        accountStatement.setDirection(TransactionDirection.DEBIT);
        accountStatement.setTotalDebitedAmount(new BigDecimal("150.00"));
        accountStatement.setChargeAmount(BigDecimal.ZERO);
        accountStatement.setCreatedAt(LocalDateTime.now());
        accountStatement.setTransactionType(TransactionType.OWN_ACCOUNT);
        accountStatement.setNotes("Payment for services");
        accountStatement.setTransactionId("TX12345");
        
        notificationContext = new NotificationContext(notificationType, user);
        
    	when(messageSource.getMessage(anyString(), any(), any())).thenReturn("email subject");
	}

    @Test
    void testSendUserCreationEmail() throws Exception {
        // Arrange
    	when(mockTemplateEngine.process(eq("create_user_email"), any())).thenReturn(htmlContent);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mockMailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        // Act
        emailNotificationService.sendUserCreationNotification(new NotificationContext(notificationType, user));

        // Assert
        verify(mockMailSender).send(mimeMessage);
        verify(mockTemplateEngine).process(eq("create_user_email"), any());
    }

    @Test
    void testSendUserLoginEmail() throws Exception {
        // Arrange
        when(mockTemplateEngine.process(eq("login_user_email"), any())).thenReturn(htmlContent);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mockMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailNotificationService.sendUserLoginNotification(new NotificationContext(notificationType, user));

        // Assert
        verify(mockMailSender).send(mimeMessage);
        verify(mockTemplateEngine).process(eq("login_user_email"), any());
    }

    @Test
    void testSendUserUpdateEmail() throws Exception {
        // Arrange
        when(mockTemplateEngine.process(eq("update_user_email"), any())).thenReturn(htmlContent);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mockMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailNotificationService.sendUserUpdateNotification(new NotificationContext(notificationType, user));

        // Assert
        verify(mockMailSender).send(mimeMessage);
        verify(mockTemplateEngine).process(eq("update_user_email"), any());
    }

    @Test
    void testSendUserDeletionEmail() throws Exception {
        // Arrange
        when(mockTemplateEngine.process(eq("delete_user_email"), any())).thenReturn(htmlContent);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mockMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailNotificationService.sendUserDeletionNotification(new NotificationContext(notificationType, user));

        // Assert
        verify(mockMailSender).send(mimeMessage);
        verify(mockTemplateEngine).process(eq("delete_user_email"), any());
    }

    @Test
    void testSendUserAccountUpdateEmail() throws Exception {
        // Arrange
        when(mockTemplateEngine.process(eq("update_account_email"), any())).thenReturn(htmlContent);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mockMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailNotificationService.sendUserAccountUpdateNotification(new NotificationContext(notificationType, user, userAccount));

        // Assert
        verify(mockMailSender).send(mimeMessage);
        verify(mockTemplateEngine).process(eq("update_account_email"), any());
    }
    
    @Test
    void testSendUserAccountTransferEmail() throws Exception {
    	// Arrange
    	when(mockTemplateEngine.process(eq("transaction_user"), any())).thenReturn(htmlContent);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mockMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailNotificationService.sendUserAccountTransferNotification(new NotificationContext(notificationType, user, userAccount, accountStatement));

        // Assert
        verify(mockMailSender).send(mimeMessage);
        verify(mockTemplateEngine).process(eq("transaction_user"), any());
    }
    
    @Test
    void testSendNotification_Failure() throws Exception {
        // Arrange
    	 when(mockTemplateEngine.process(eq("create_user_email"), any())).thenReturn(htmlContent);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mockMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Use a RuntimeException instead of a checked exception
        doThrow(new RuntimeException("Simulated failure")).when(mockMailSender).send(mimeMessage);
        
        // Act
        emailNotificationService.sendUserCreationNotification(notificationContext);

        // Assert
        verify(userRepository, times(1)).flush();
        verify(notificationContextRepository, times(1)).save(notificationContext);
        verify(mockFailedNotificationRepository, times(1)).save(any(FailedNotification.class));
    }
    
    @Test
    void testSaveFailedNotification() throws Exception {
        // Act
        emailNotificationService.saveFailedNotification(notificationContext, subject, htmlContent, reason);

        // Assert
        verify(userRepository, times(1)).flush();
        verify(notificationContextRepository, times(1)).save(notificationContext);
        verify(mockFailedNotificationRepository, times(1)).save(any(FailedNotification.class));
    }
    
    @Test
    void testRetryNotification() throws Exception {
    	// Arrange
        FailedNotification failedNotification = new FailedNotification(notificationContext, subject, template, reason);

    	when(mockTemplateEngine.process(eq(failedNotification.getTemplate()), any())).thenReturn(htmlContent);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mockMailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        // Act
        emailNotificationService.retryNotification(failedNotification);

        // Assert
        verify(mockMailSender).send(mimeMessage);
        verify(mockTemplateEngine).process(eq(failedNotification.getTemplate()), any());
    }


    
}

