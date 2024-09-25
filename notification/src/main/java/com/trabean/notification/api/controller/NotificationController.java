package com.trabean.notification.api.controller;


import com.trabean.notification.api.service.NotificationService;
import com.trabean.notification.db.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")

public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok().body("Hello World");
    }

    @GetMapping("/allNotification")
    public ResponseEntity<?> getAllNotification() {
        List<Notification> notifications = notificationService.getNotifications();

        return ResponseEntity.ok().body("으갸갸갸갹");
    }
}
