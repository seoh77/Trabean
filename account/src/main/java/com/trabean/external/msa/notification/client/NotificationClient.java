package com.trabean.external.msa.notification.client;

import com.trabean.config.feign.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "notification", configuration = FeignClientConfig.class)
public interface NotificationClient {
}
