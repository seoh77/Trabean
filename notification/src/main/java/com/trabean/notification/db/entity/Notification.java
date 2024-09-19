package com.trabean.notification.db.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private Long senderId;
    private Long receiverId;
    private Long accountId;
    private NotificationType type;
    private boolean isRead;
    private Long amount;
    private Timestamp createTime;


}
