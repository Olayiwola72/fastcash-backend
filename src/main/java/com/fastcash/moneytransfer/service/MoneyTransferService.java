package com.fastcash.moneytransfer.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fastcash.moneytransfer.enums.NotificationType;
import com.fastcash.moneytransfer.enums.TransactionDirection;
import com.fastcash.moneytransfer.exception.InsufficientBalanceException;
import com.fastcash.moneytransfer.model.AccountStatement;
import com.fastcash.moneytransfer.model.Admin;
import com.fastcash.moneytransfer.model.BaseUser;
import com.fastcash.moneytransfer.model.MoneyTransfer;
import com.fastcash.moneytransfer.model.NotificationContext;
import com.fastcash.moneytransfer.model.TransactionAccount;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.repository.MoneyTransferRepository;
import com.fastcash.moneytransfer.validation.UserAccountMismatchValidator;

@Service
public class MoneyTransferService {
	private final String email;
	private final MoneyTransferRepository moneyTransferRepository;
	private final UserService userService;
	private final InternalMoneyTransferService internalMoneyTransferService;
	private final AccountService accountService;
	private final AccountStatementService accountStatementService;
	private final EmailNotifiable emailNotifiable;
	private UserAccount debitAccount;
	private TransactionAccount creditAccount;
	
	public MoneyTransferService(
		@Value("${app.admin.email}") String email, 
		MoneyTransferRepository moneyTransferRepository, 
		UserService userService, 
		InternalMoneyTransferService internalMoneyTransferService, 
		AccountService accountService,
		AccountStatementService accountStatementService,
		EmailNotifiable emailNotifiable
	) {
		this.email = email;
		this.moneyTransferRepository = moneyTransferRepository;
		this.userService = userService;
		this.internalMoneyTransferService = internalMoneyTransferService;
		this.accountService = accountService;
		this.accountStatementService = accountStatementService;
		this.emailNotifiable = emailNotifiable;
	}
	
	@Transactional(
		rollbackFor = Exception.class, 
		propagation = Propagation.REQUIRED, 
		isolation = Isolation.REPEATABLE_READ
	)
	public MoneyTransfer create(MoneyTransfer moneyTransfer, User debitedUser) throws InsufficientBalanceException {
		BaseUser creditedUser = null;
		debitAccount = moneyTransfer.getDebitAccount();
		
        // Check if the debit account belongs to the currently logged in user
        UserAccountMismatchValidator.handleMismatch(debitAccount, debitedUser, "debitAccount");
        
		if(moneyTransfer.getTransactionType().isInternal()) {
			creditedUser = findUserByAccountId((UserAccount) moneyTransfer.getCreditAccount(), debitedUser);
			creditAccount = (TransactionAccount) moneyTransfer.getCreditAccount();
            // update credit account balance if own account
			handleOwnAccountTransfer(moneyTransfer);
		}else {
			creditedUser = userService.findAdminByEmail(email).get();
			moneyTransfer = internalMoneyTransferService.handleInternalTransfer(debitAccount, moneyTransfer, (Admin) creditedUser);
			creditAccount = (TransactionAccount) moneyTransfer.getInternalAccount();
		}
		
		// Save the transfer
        return save(moneyTransfer, debitedUser, creditedUser);
	}
	
	private void handleOwnAccountTransfer(MoneyTransfer moneyTransfer) throws InsufficientBalanceException {
		// Perform the transfer within a single transaction
        accountService.transferFunds(debitAccount, creditAccount, moneyTransfer.getTotalDebitedAmount(), moneyTransfer.getTotalCreditedAmount());
    }
	
	public MoneyTransfer save(MoneyTransfer moneyTransfer, User user, BaseUser creditedUser) {    
        moneyTransfer = moneyTransferRepository.save(moneyTransfer);

        AccountStatement accountStatementCredit = createAccountStatement(TransactionDirection.CREDIT, moneyTransfer, creditedUser, creditAccount);
        AccountStatement accountStatementDebit = createAccountStatement(TransactionDirection.DEBIT, moneyTransfer, user, debitAccount);
        
        // Send email
        emailNotifiable.sendUserAccountTransferNotification(
        	new NotificationContext(
        		NotificationType.EMAIL, 
        		user, 
        		moneyTransfer.getDebitAccount(), 
        		accountStatementDebit
        	)
        );
        
        if(creditedUser instanceof User) {
        	emailNotifiable.sendUserAccountTransferNotification(
        		new NotificationContext(NotificationType.EMAIL, 
        		(User) creditedUser, 
        		(TransactionAccount) moneyTransfer.getCreditAccount(), accountStatementCredit)
        	);
        }

        return moneyTransfer;
    }

    private User findUserByAccountId(UserAccount creditedAccount, User debitedUser) {
        Optional<UserAccount> optionalAccount = debitedUser.getAccounts().stream()
                .filter(account -> account.getId().equals(creditedAccount.getId()))
                .findFirst();

        return optionalAccount.isPresent() ? debitedUser : userService.findUserByAccountId(creditedAccount.getId());
    }

    private AccountStatement createAccountStatement(TransactionDirection direction, MoneyTransfer moneyTransfer, BaseUser user, TransactionAccount account) {
        AccountStatement accountStatement = new AccountStatement(direction, moneyTransfer, user, account);
        return accountStatementService.create(accountStatement);
    }
    
}
