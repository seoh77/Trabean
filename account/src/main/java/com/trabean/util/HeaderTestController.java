package com.trabean.util;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/accounts/test1")
public class HeaderTestController {

    @GetMapping
    public Map<String, Header> test() {
        return Map.of("Header", Header.builder()
                .apiName("김치치즈")
                .userKey("testUserKey1")
                .build());
    }
}
