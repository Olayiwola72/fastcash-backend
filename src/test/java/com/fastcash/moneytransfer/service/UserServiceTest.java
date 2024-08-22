package com.fastcash.moneytransfer.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.model.AccountStatement;
import com.fastcash.moneytransfer.model.MoneyTransfer;
import com.fastcash.moneytransfer.model.NotificationContext;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.repository.FailedNotificationRepository;
import com.fastcash.moneytransfer.repository.RefreshTokenRepository;
import com.fastcash.moneytransfer.repository.UserRepository;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private FailedNotificationRepository failedNotificationRepository;

    @Mock
    private EmailNotifiable emailNotifiable;

    @InjectMocks
    private UserService userService; // Assume your service class is named UserService

    private User user; // Assume this is your User entity
    private final String email = "john.doe@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize your user object here
        user = new User();
        user.setId(1L); // example user id
        user.setEmail(email);

        // Set up accounts, transfers, and statements
        UserAccount account = new UserAccount(Currency.NGN, user);
        MoneyTransfer transfer = new MoneyTransfer();
        AccountStatement statement = new AccountStatement();

        user.setAccounts(Arrays.asList(account));
        user.setTransfers(Arrays.asList(transfer));
        user.setAccountStatements(Arrays.asList(statement));
    }

    @Test
    void testAsyncSoftDeleteUserById() throws Exception {
        // Mock userRepository.save() to return the user
        when(userRepository.save(user)).thenReturn(user);

        // Call the method under test
        CompletableFuture<Void> future = userService.asyncSoftDeleteUserById(user);

        // Wait for the asynchronous process to complete
        future.join(); // This will wait for the async method to complete

        // Verify email notification was sent
        verify(emailNotifiable, times(1)).sendUserDeletionNotification(any(NotificationContext.class));

        // Verify associated entities are soft deleted
        user.getAccounts().forEach(account -> assertTrue(account.isDeleted()));
        user.getTransfers().forEach(transfer -> assertTrue(transfer.isDeleted()));
        user.getAccountStatements().forEach(statement -> assertTrue(statement.isDeleted()));
        
        // Verify user was saved
        verify(userRepository, times(1)).save(user);

        // Verify that deleteAllByUser is called on both repositories with the correct user
        verify(refreshTokenRepository, times(1)).deleteAllByUser(user);
        verify(failedNotificationRepository, times(1)).deleteAllByUser(user);
    }
    
}

