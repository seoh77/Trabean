package com.trabean.util;

import lombok.Builder;
import lombok.Data;

import java.security.SecureRandom;

@Data
@Builder
public class RequestHeader {
    String apiName;
    String transmissionDate;
    String transmissionTime;
    final String institutionCode = "00100";
    final String fintechAppNo = "001";
    String apiServiceCode;
    String institutionTransactionUniqueNo;
    String apiKey;
    String userKey;

    public static class RequestHeaderBuilder {
        public RequestHeaderBuilder apiName(String apiName) {
            CurrentTime currentTime = new CurrentTime();

            this.apiName = apiName;
            this.transmissionDate = currentTime.getYYYYMMDD();
            this.transmissionTime = currentTime.getHHMMSS();
            this.apiServiceCode = apiName;
            this.institutionTransactionUniqueNo = createInstitutionTransactionUniqueNo(currentTime.getYYYYMMDDHHMMSS());
            this.apiKey = System.getenv("API_KEY");
            return this;
        }
    }

    private static class RandomStringGenerator {
        private static final String NUMBERS = "0123456789";

        public static String generateRandomString(String YYYMMDDHHMMSS) {
            SecureRandom random = new SecureRandom();
            StringBuilder sb = new StringBuilder(20);

            sb.append(YYYMMDDHHMMSS);

            for (int i = 0; i < 6; i++) {
                int index = random.nextInt(NUMBERS.length());
                sb.append(NUMBERS.charAt(index));
            }

            return sb.toString();
        }
    }

    private static String createInstitutionTransactionUniqueNo(String YYYMMDDHHMMSS) {
        return RandomStringGenerator.generateRandomString(YYYMMDDHHMMSS);
    }
}
