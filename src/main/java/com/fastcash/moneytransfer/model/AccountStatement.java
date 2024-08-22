package com.fastcash.moneytransfer.model;

import java.math.BigDecimal;

import com.fastcash.moneytransfer.enums.TransactionDirection;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class AccountStatement extends TransferDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="money_transfer_id", nullable = false)
	@JsonBackReference
    private MoneyTransfer moneyTransfer;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
	@JsonBackReference
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="admin_id")
	@JsonBackReference
	private Admin admin;
	
	@NotNull
	private TransactionDirection direction;
	
	@Column(precision = 10, scale = 3)
	private BigDecimal balance;
	
	public AccountStatement() {}
	
	public AccountStatement(TransactionDirection direction, MoneyTransfer moneyTransfer, BaseUser user, TransactionAccount account) {
		super();
		this.setDirection(direction);
		this.setMoneyTransfer(moneyTransfer);
		if(user instanceof User) {
			this.setUser((User) user);
		}else {
			this.setUser((Admin) user);
		}
		this.setTransactionId(moneyTransfer.getTransactionId());
		this.setAmount(moneyTransfer.getAmount());
		this.setNotes(moneyTransfer.getNotes());
		this.setDebitCurrency(moneyTransfer.getDebitCurrency());
		this.setCreditCurrency(moneyTransfer.getCreditCurrency());
		this.setDebitAccount(moneyTransfer.getDebitAccount());
		this.setConversionRate(moneyTransfer.getConversionRate());
		this.setTotalDebitedAmount(moneyTransfer.getTotalDebitedAmount());
		this.setTotalCreditedAmount(moneyTransfer.getTotalCreditedAmount());
		this.setChargeAmount(moneyTransfer.getChargeAmount());
		this.setInternalAccount(moneyTransfer.getInternalAccount());
		this.setInternalChargeAccount(moneyTransfer.getInternalChargeAccount());
		this.setTransactionType(moneyTransfer.getTransactionType());
		
		// Use the getCreditAccount() method to set the correct type of credit account
        if (moneyTransfer.getCreditAccount() instanceof UserAccount) {
            this.setCreditAccount((UserAccount) moneyTransfer.getCreditAccount());
        } else {
            this.setCreditAccount((ExternalAccount) moneyTransfer.getCreditAccount());
        }
        
        this.balance = account.getBalance();
	}
	
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public TransactionDirection getDirection() {
		return direction;
	}

	public void setDirection(TransactionDirection direction) {
		this.direction = direction;
	}

	public MoneyTransfer getMoneyTransfer() {
		return moneyTransfer;
	}

	public void setMoneyTransfer(MoneyTransfer moneyTransfer) {
		this.moneyTransfer = moneyTransfer;
	}

	public BaseUser getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public void setUser(Admin admin) {
		this.admin = admin;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
}
