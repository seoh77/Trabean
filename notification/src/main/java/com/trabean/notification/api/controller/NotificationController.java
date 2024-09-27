package com.trabean.notification.api.controller;


import com.trabean.notification.api.dto.request.NotificationSaveReq;
import com.trabean.notification.api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")

public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("")
    public ResponseEntity<?> saveNotification(@RequestBody NotificationSaveReq notificationSaveReq) {
        notificationService.saveNotification(notificationSaveReq);
        return ResponseEntity.ok().build();
    }


}
