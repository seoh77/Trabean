package com.trabean.notification.api.dto.feignClient;


import lombok.*;


@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor

public class TestDto {

    private String text;

    public TestDto(String text) {
        this.text = text;
    }
}
