package com.trabean.util;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class CurrentTime {
    private String yyyymmddhhmmss;
    private String yyyymmdd;
    private String hhmmss;

    public CurrentTime() {
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");

        this.yyyymmddhhmmss = now.format(dateTimeFormatter);
        this.yyyymmdd = now.format(dateFormatter);
        this.hhmmss = now.format(timeFormatter);
    }
}
