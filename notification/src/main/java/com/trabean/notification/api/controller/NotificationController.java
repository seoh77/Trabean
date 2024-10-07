package com.trabean.notification.api.controller;


import com.trabean.notification.api.dto.request.NotificationCreateReq;
import com.trabean.notification.api.dto.response.NotificationReadRes;
import com.trabean.notification.api.service.NotificationService;
import com.trabean.notification.interceptor.UserHeaderInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("")
    public ResponseEntity<?> saveNotification(@RequestBody NotificationCreateReq notificationCreateReq) {
        log.info("@@@@@@@@@@@@@컨트롤러진입 (알림생성)");
        notificationService.saveNotification(notificationCreateReq);
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<?> getNotification() {
        List<NotificationReadRes> notificationReadResList = notificationService.findByUserId(UserHeaderInterceptor.userId.get());
        return ResponseEntity.ok().body(notificationReadResList);
    }

    @PatchMapping("/{notificationId}")
    public ResponseEntity<?> updateIsRead(@PathVariable Long notificationId) {
        notificationService.updateIsReadById(notificationId);
        return ResponseEntity.ok().build();
    }
}
