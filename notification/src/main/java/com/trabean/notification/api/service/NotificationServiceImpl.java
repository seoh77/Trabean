package com.trabean.notification.api.service;


import com.trabean.notification.api.dto.request.NotificationCreateReq;
import com.trabean.notification.api.dto.response.NotificationReadRes;
import com.trabean.notification.db.entity.Notification;
import com.trabean.notification.db.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final NotificationRepository notificationRepository;

    @Override
    public void saveNotification(NotificationCreateReq notificationCreateReq) {
        Notification notification = notificationCreateReq.toEntity();
        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationReadRes> findByUserId(Long userId) {
        List<Notification> notificationList = notificationRepository.findByReceiverId(userId);
        return notificationList.stream().map(Notification::toReadDto).toList();
    }
}
