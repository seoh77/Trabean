package com.trabean.config.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trabean.exception.CustomFeignClientException;
import com.trabean.exception.InternalServerStatusException;
import com.trabean.exception.dto.SsafyServerErrorResponseDTO;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;

public class FeignClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            if(methodKey.startsWith("DomesticClient") ||
                    methodKey.startsWith("ForeignClient") ||
                    methodKey.startsWith("VerificationClient")) {
                SsafyServerErrorResponseDTO errorResponse = objectMapper.readValue(response.body().asInputStream(), SsafyServerErrorResponseDTO.class);
                return new CustomFeignClientException(errorResponse);
            }
            return new InternalServerStatusException("범인 -> " + methodKey);
        } catch (IOException e) {
            return new RuntimeException(e.getMessage());
        }
    }

}
