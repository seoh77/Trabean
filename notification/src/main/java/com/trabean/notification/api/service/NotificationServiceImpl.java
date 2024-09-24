package com.trabean.notification.api.service;


import com.trabean.notification.db.entity.Notification;
import com.trabean.notification.db.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
    private final NotificationRepository notificationRepository;
    @Override
    public List<Notification> getNotifications() {
        return notificationRepository.findAll();
    }
}
