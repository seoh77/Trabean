package com.trabean.notification.db.repository;

import com.trabean.notification.db.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
