package com.trabean.payment.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trabean.payment.dto.response.FeignErrorResponse;
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
                if (errorResponse.getMessage().equals("비밀번호가 틀렸습니다.")) {

                }
                return new PaymentsException(errorResponse.getMessage(), HttpStatus.BAD_GATEWAY);
            }
            return new PaymentsException("범인 : 싸피 ", HttpStatus.BAD_GATEWAY);
        } catch (IOException e) {
            return new RuntimeException(e.getMessage());
        }
    }

}
