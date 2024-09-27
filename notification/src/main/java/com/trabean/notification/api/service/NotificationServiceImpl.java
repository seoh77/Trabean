package com.trabean.notification.api.service;


import com.trabean.notification.api.dto.request.NotificationSaveReq;
import com.trabean.notification.db.entity.Notification;
import com.trabean.notification.db.repository.NotificationRepository;
import com.trabean.notification.exception.NotificationSaveException;
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
        try {
            notificationRepository.save(notification);
        } catch (Exception e) {
            log.error("알림 생성중 DB 에러");
            throw NotificationSaveException.instance;
        }
    }
}
