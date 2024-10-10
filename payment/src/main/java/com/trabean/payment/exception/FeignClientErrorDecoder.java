package com.trabean.payment.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import org.springframework.http.HttpStatus;

public class FeignClientErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            if (methodKey.startsWith("AccountClient") ||
                    methodKey.startsWith("NotificationClient") ||
                    methodKey.startsWith("TravelClient") ||
                    methodKey.startsWith("UserClient")) {
                PaymentsException errorResponse = objectMapper.readValue(response.body().asInputStream(),
                        PaymentsException.class);
                return new PaymentsException(errorResponse.getMessage(), HttpStatus.BAD_GATEWAY);
            }
            return new PaymentsException("범인 :싸피 ", HttpStatus.BAD_GATEWAY);
        } catch (IOException e) {
            return new RuntimeException(e.getMessage());
        }
    }

}
