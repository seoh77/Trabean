package com.trabean.payment.client;

import com.trabean.payment.dto.request.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "travel", configuration = FeignClientConfiguration.class)
public interface NotificationClient {

    @PostMapping(value = "/api/notifications", consumes = MediaType.APPLICATION_JSON_VALUE)
    void sendNotification(@RequestBody NotificationRequest notificationRequest);
}
