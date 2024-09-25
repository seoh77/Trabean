package com.trabean.notification.api.controller;

import com.trabean.notification.api.dto.feignClient.TestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "notification", path = "/api")
public interface FeignController {
    @GetMapping("/notification/test")
    TestDto tttt();

}
