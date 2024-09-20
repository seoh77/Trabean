package com.trabean.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Header {
    String apiName;
    String transmissionDate;
    String transmissionTime;
    final String institutionCode = "00100";
    final String fintechAppNo = "001";
    String apiServiceCode;
    String institutionTransactionUniqueNo;
    String apiKey;
    String userKey;

    public static class HeaderBuilder {
        public HeaderBuilder apiName(String apiName) {
            CurrentTime currentTime = new CurrentTime();

            this.apiName = apiName;
            this.transmissionDate = currentTime.getYyyymmdd();
            this.transmissionTime = currentTime.getHhmmss();
            this.apiServiceCode = apiName;
            this.institutionTransactionUniqueNo = createInstitutionTransactionUniqueNo(currentTime.getYyyymmddhhmmss());
            this.apiKey = System.getenv("API_KEY");
            return this;
        }
    }

    private static String createInstitutionTransactionUniqueNo(String currentTime) {
        return currentTime + RandomStringGenerator.generateRandomString();
    }
}
