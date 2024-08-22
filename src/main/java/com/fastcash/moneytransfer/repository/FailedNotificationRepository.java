package com.fastcash.moneytransfer.repository;

import com.fastcash.moneytransfer.model.FailedNotification;
import com.fastcash.moneytransfer.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedNotificationRepository extends JpaRepository<FailedNotification, Long> {
	void deleteAllByUser(User user);
}
