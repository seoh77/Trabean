package com.trabean.payment.client;

import com.trabean.payment.FeignClientConfig;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "travel", configuration = FeignClientConfig.class)
public interface TravelClient {

    @GetMapping(value = "/api/travel/currency", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Long> getFORAccount(
            @RequestParam("accountId") Long accountId,
            @RequestParam("currency") String currency
    );
}
