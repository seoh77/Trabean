package com.trabean.util;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class CurrentTime {
    private String YYYYMMDDHHMMSS;
    private String YYYYMMDD;
    private String HHMMSS;

    public CurrentTime() {
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");

        this.YYYYMMDDHHMMSS = now.format(dateTimeFormatter);
        this.YYYYMMDD = now.format(dateFormatter);
        this.HHMMSS = now.format(timeFormatter);
    }
}
