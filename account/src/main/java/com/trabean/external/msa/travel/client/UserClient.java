
package com.trabean.external.msa.travel.client;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "userClient", url = "http://j11a604.p.ssafy.io:8086/api/user")
public interface UserClient {

    @GetMapping(value = "/name", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, String> getUserName(@RequestParam Long userId);
}