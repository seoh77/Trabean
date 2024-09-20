package com.trabean.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts/test")
public class HeaderTestController {

    @Value("${API_KEY}")
    private String API_KEY;

    @GetMapping()
    public Header test() {
        return Header.
                builder()
                .apiName("김치")
                .apiKey(API_KEY)
                .build();
    }
}
