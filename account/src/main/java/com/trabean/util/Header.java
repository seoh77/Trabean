package com.trabean.util;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder

public class Header {
    String apiName;

    @Builder.Default
    String transmissionDate = getCurrentDate();

    @Builder.Default
    String transmissionTime = getCurrentTime();

    final String institutionCode = "00100";
    final String fintechAppNo = "001";

    @Builder.Default
    String apiServiceCode;

    String institutionTransactionUniqueNo;

    @Builder.Default
    String apiKey;

    String userKey;

    private static String getCurrentDate(){
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    private static String getCurrentTime(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
    }
}
