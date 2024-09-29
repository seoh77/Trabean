package com.trabean.external.msa.notification.client;

import com.trabean.config.FeignClientConfig;
import com.trabean.external.msa.notification.dto.request.NotificationRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "notification", configuration = FeignClientConfig.class)
public interface NotificationClient {

    /**
     * 입출금 시 알림 생성 요청
     */
    void sendNotification(NotificationRequestDTO requestDTO);
}
