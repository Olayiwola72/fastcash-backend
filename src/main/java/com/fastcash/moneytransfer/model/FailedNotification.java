package com.fastcash.moneytransfer.model;

import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "failed_notifications")
public class FailedNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @JoinColumn(nullable = false)
    @OneToOne(targetEntity = NotificationContext.class, fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private NotificationContext notificationContext;
    
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private User user;

    private String subject; // For emails
    
    @NotNull
    @Column(nullable = false)
    private String template; // Email body or SMS template
    
    @Column(nullable = false)
    private int retryCount = 0;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(nullable = false)
    private Date lastAttemptDate;

    private String failureReason;

    public FailedNotification() {}

    public FailedNotification(NotificationContext notificationContext, String subject, String template, String failureReason) {
    	this.setUser(notificationContext.getUser());
        this.setNotificationContext(notificationContext);
        this.setSubject(subject);
        this.setTemplate(template);
        this.setFailureReason(failureReason);
        this.setLastAttemptDate(new Date());
    }
	
	public NotificationContext getNotificationContext() {
		return notificationContext;
	}

	public void setNotificationContext(NotificationContext notificationContext) {
		this.notificationContext = notificationContext;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public Date getLastAttemptDate() {
		return lastAttemptDate;
	}

	public void setLastAttemptDate(Date lastAttemptDate) {
		this.lastAttemptDate = lastAttemptDate;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}
	
    public void incrementRetryCount() {
        this.setRetryCount(this.getRetryCount() + 1);
    }
    
}
