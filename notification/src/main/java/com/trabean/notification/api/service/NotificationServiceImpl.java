package com.trabean.notification.api.service;


import com.trabean.notification.api.dto.request.NotificationSaveReq;
import com.trabean.notification.db.entity.Notification;
import com.trabean.notification.db.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final NotificationRepository notificationRepository;

    @Override
    public void saveNotification(NotificationSaveReq notificationSaveReq) {
        Notification notification = notificationSaveReq.toEntity();
        notificationRepository.save(notification);
    }
}
