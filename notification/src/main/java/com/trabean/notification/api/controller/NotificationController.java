package com.trabean.notification.api.controller;


import com.trabean.notification.api.dto.feignClient.TestDto;
import com.trabean.notification.api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")

public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;
    private final FeignController feignController;

    @GetMapping("/hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok().body("Hello World");
    }

    @GetMapping("/allNotification")
    public ResponseEntity<?> getAllNotification() {
        logger.info("/allNotification  여기왔따!!!!!!!!!!!!!!!!"); // 로그 추가
        TestDto testDto = feignController.tttt();
        logger.info("여기는 안와싸!!!!!!!!!!!!!!!!"); // 로그 추가

        return ResponseEntity.ok().body(testDto);
    }


    @GetMapping("/test")
    public ResponseEntity<?> test() {
        logger.info("/test  여기왔따11111111111111111111"); // 로그 추가
        TestDto testDto = new TestDto();
        testDto.setText("1111111111111111111");

        return ResponseEntity.ok().body(testDto);
    }

}
