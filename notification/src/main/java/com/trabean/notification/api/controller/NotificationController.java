package com.trabean.notification.api.controller;


import com.trabean.notification.api.dto.request.NotificationCreateReq;
import com.trabean.notification.api.dto.response.NotificationReadRes;
import com.trabean.notification.api.feign.UserFeign;
import com.trabean.notification.api.service.NotificationService;
import com.trabean.notification.interceptor.UserHeaderInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final UserFeign userFeign;

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


    @GetMapping(value = "/status", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter handleSse() {
        
        SseEmitter sseEmitter = new SseEmitter(60000L); // 60초 타임아웃 설정

        new Thread(() -> {
            try {
                while (true) {
                    String result = notificationService.getStatus(UserHeaderInterceptor.userId.get()) ? "1" : "0";
                    sseEmitter.send(result);
                    log.info("정보를 한번 보내볼게요 : " + result);
                    Thread.sleep(5000); // 5초마다 체크
                }
            } catch (IOException | InterruptedException e) {
                log.info("정보 보내는 거 종료");
                sseEmitter.completeWithError(e);
            }
            log.info("정보 보내는 거 종료");
        }).start();
        log.info("정보 보내는 거 종료");
        return sseEmitter;
    }
//
//    @GetMapping("/test")
//    public ResponseEntity<?> test() {
//        System.out.println("들어옴");
//        UserKeyReq userKeyReq = new UserKeyReq();
//        userKeyReq.setUserId(8L);
//        UserKeyRes userKey = userFeign.getUserKey(userKeyReq);
//        return ResponseEntity.ok().body(userKey);
//    }
}
