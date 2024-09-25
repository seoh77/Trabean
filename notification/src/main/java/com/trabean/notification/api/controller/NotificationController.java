package com.trabean.notification.api.controller;


import com.trabean.notification.api.dto.feignClient.TestDto;
import com.trabean.notification.api.service.NotificationService;
import com.trabean.notification.db.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")

public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;

    @GetMapping("/hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok().body("Hello World");
    }

    @GetMapping("/allNotification")
    public ResponseEntity<?> getAllNotification() {
        logger.info("여기왔따!!!!!!!!!!!!!!!!"); // 로그 추가


        List<Notification> notifications = notificationService.getNotifications();
        TestDto dto = new TestDto("으갸갸갸갸");
        return ResponseEntity.ok().body(dto);
    }
}
