package com.trabean.payment.client;

import com.trabean.payment.FeignClientConfig;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user", configuration = FeignClientConfig.class)
public interface UserClient {

    @GetMapping(value = "api/user/paymentaccount/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Long> getPaymentAccount(@PathVariable("userId") Long userId);

    @GetMapping(value = "api/user/name/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, String> getUserName(@PathVariable("userId") Long userId);
}
