package com.trabean.payment.exception;
import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.ws.rs.InternalServerErrorException;
import org.springframework.http.HttpStatus;

public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {

        return switch (response.status()) {
            case 400 -> new PaymentsException("feign: data 를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
            case 500 -> new PaymentsException("feign: 서버 내부에 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            default -> new Exception(response.reason());
        };
    }
}
