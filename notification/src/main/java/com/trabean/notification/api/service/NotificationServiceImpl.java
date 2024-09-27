package com.trabean.notification.api.service;


import com.trabean.notification.api.dto.request.NotificationCreateReq;
import com.trabean.notification.api.dto.response.NotificationReadRes;
import com.trabean.notification.db.entity.Notification;
import com.trabean.notification.db.repository.NotificationRepository;
import com.trabean.notification.exception.NotificationReadException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public void updateIsReadById(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> NotificationReadException.instance);
        notification.setRead(true);
    }
}
