package com.trabean.payment.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trabean.payment.dto.response.FeignErrorResponse;
import com.trabean.payment.dto.response.SsafyErrorResponse;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class FeignClientErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            if (methodKey.startsWith("AccountClient") ||
                    methodKey.startsWith("NotificationClient") ||
                    methodKey.startsWith("TravelClient") ||
                    methodKey.startsWith("UserClient")) {
                FeignErrorResponse errorResponse = objectMapper.readValue(response.body().asInputStream(),
                        FeignErrorResponse.class);
                log.info(errorResponse + "@@@@@@@@@@@@@@@");
                return new PaymentsException(errorResponse.getMessage() + methodKey, HttpStatus.BAD_GATEWAY);
            }

            if (methodKey.startsWith("DemandDepositClient") || methodKey.startsWith("ExchangeClient")) {
                SsafyErrorResponse errorResponse = objectMapper.readValue(response.body().asInputStream(),
                        SsafyErrorResponse.class);
                return new SsafyException(errorResponse.getResponseMessage() + methodKey,
                        errorResponse.getResponseCode());
            }
            FeignErrorResponse errorResponse = objectMapper.readValue(response.body().asInputStream(),
                    FeignErrorResponse.class);
            return new PaymentsException(errorResponse.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            return new RuntimeException(e.getMessage());
        }
    }

}
