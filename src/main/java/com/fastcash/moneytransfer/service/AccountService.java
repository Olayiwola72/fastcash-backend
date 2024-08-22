package com.fastcash.moneytransfer.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.NotificationType;
import com.fastcash.moneytransfer.exception.InsufficientBalanceException;
import com.fastcash.moneytransfer.model.NotificationContext;
import com.fastcash.moneytransfer.model.TransactionAccount;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.repository.TransactionAccountRepository;
import com.fastcash.moneytransfer.repository.UserAccountRepository;
import com.fastcash.moneytransfer.validation.BalanceValidator;

@Service
public class AccountService {
    private final UserAccountRepository userAccountRepository;
    private final TransactionAccountRepository transactionAccountRepository;
    private final EmailNotifiable emailNotifiable;

    public AccountService(
    	UserAccountRepository userAccountRepository,
    	TransactionAccountRepository transactionAccountRepository,
    	EmailNotifiable emailNotifiable
    ) {
        this.userAccountRepository = userAccountRepository;
        this.transactionAccountRepository = transactionAccountRepository;
        this.emailNotifiable = emailNotifiable;
    }
    
    public Optional<UserAccount> findById(Long id) {
        return Optional.ofNullable(
        	userAccountRepository.findById(id)
        		.orElseThrow(() -> new ObjectNotFoundException("account", id))
        );
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public List<UserAccount> create(User user){
        List<UserAccount> userAccounts = new ArrayList<>();
        
        for(Currency currency : Currency.values()) {
        	UserAccount userAccount = new UserAccount(currency, user);
        	userAccounts.add(userAccount);
        }
        
        return userAccountRepository.saveAll(userAccounts);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public User update(UserAccount userAccount, UserService userService){
        UserAccount existingAccount = findById(userAccount.getId()).get();
        User user = userService.findUserByAccountId(userAccount.getId());
		
	    // Update account properties
        existingAccount.setAllowOverdraft(userAccount.isAllowOverdraft());
	    userAccountRepository.save(existingAccount);
	    
	    emailNotifiable.sendUserAccountUpdateNotification(new NotificationContext(NotificationType.EMAIL, user, existingAccount));
	    
	    return user;
    }
    
    @Transactional(propagation = Propagation.MANDATORY)
    public void transferFunds(UserAccount fromAccount, TransactionAccount toAccount, BigDecimal debitAmount, BigDecimal creditAmount) throws InsufficientBalanceException {
        // Perform withdrawal
        withdraw(fromAccount, debitAmount);
        
        // Perform deposit
        deposit(toAccount, creditAmount);
    }
    
    @Transactional(propagation = Propagation.MANDATORY)
    public void withdraw(UserAccount fromAccountId, BigDecimal amount) throws InsufficientBalanceException {
    	BigDecimal newBalance = fromAccountId.getBalance().subtract(amount);
    	BalanceValidator.validateBalance(newBalance, fromAccountId, amount);
    	
    	fromAccountId.setBalance(newBalance);
    	userAccountRepository.save(fromAccountId);
    }
    
    @Transactional(propagation = Propagation.MANDATORY)
    public void deposit(TransactionAccount toAccountId, BigDecimal amount) {
    	BigDecimal newBalance = toAccountId.getBalance().add(amount);
    	toAccountId.setBalance(newBalance);
    	transactionAccountRepository.save(toAccountId);
    }
    
}
