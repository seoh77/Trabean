package com.trabean.notification.api.service;


import com.trabean.notification.api.dto.request.NotificationSaveReq;

public interface NotificationService {

    public void saveNotification(NotificationSaveReq notificationSaveReq);
}
