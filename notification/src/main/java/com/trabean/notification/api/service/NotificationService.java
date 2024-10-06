package com.trabean.notification.api.service;


import com.trabean.notification.db.entity.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> getNotifications();
}
