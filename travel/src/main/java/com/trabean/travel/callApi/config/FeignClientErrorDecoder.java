package com.trabean.travel.callApi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trabean.travel.callApi.dto.ErrorResponseDTO;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;

public class FeignClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        ObjectMapper objectMapper = new ObjectMapper();

        if (response.body() == null) {
            return new CustomFeignClientException(new ErrorResponseDTO(response.status() + "", "응답 본문이 없습니다."));
        }

        try {
            ErrorResponseDTO errorResponse = objectMapper.readValue(response.body().asInputStream(),
                    ErrorResponseDTO.class);
            return new CustomFeignClientException(errorResponse);
        } catch (IOException e) {
            return new RuntimeException(e.getMessage());
        }
    }

}
