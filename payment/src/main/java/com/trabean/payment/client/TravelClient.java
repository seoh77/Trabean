package com.trabean.payment.client;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "travel", configuration = FeignClientConfiguration.class)
public interface TravelClient {

    @PostMapping(value = "/api/travel/currency", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Long> getFORAccount(
            @RequestParam("accountId") Long accountId,
            @RequestParam("currency") String currency
    );
}
