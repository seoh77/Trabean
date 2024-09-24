package com.trabean.notification.db.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "notifications")
@Data
public class Notification {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private Long senderId;
    private Long receiverId;
    private Long accountId;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    private boolean isRead;
    private Long amount;
    private Timestamp createTime;


}
