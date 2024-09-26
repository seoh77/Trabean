package com.trabean.payment.client;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "travelClient", url = "http://j11a604.p.ssafy.io:8085/api/travel")
public interface TravelClient {

    @PostMapping(value = "/currency", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Long> getFORAccount(
            @RequestParam("accountId") Long accountId,
            @RequestParam("currency") String currency
    );
}
