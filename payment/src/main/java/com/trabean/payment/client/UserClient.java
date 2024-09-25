package com.trabean.payment.client;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "userClient", url = "http://j11a604.p.ssafy.io:8086/api")
public interface UserClient {

    @GetMapping(value = "/user/paymentaccount/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Long> getPaymentAccount(@PathVariable("userId") Long userId);
}
