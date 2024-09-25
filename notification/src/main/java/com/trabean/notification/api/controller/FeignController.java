package com.trabean.notification.api.controller;

import com.trabean.notification.api.dto.feignClient.TestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "notification", url = "http://j11a604.p.ssafy.io:8083", path = "/api")
public interface FeignController {
    @GetMapping("/notification/test")
    TestDto tttt();

}
