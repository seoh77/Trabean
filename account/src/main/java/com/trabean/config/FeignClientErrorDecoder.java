package com.trabean.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trabean.external.ssafy.common.SsafyApiResponseDTO;
import com.trabean.exception.custom.SsafyErrorException;
import com.trabean.exception.custom.ExternalServerErrorException;
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
                    methodKey.startsWith("VerificationClient") ||
                    methodKey.startsWith("MemoClient")) {
                SsafyApiResponseDTO errorResponse = objectMapper.readValue(response.body().asInputStream(), SsafyApiResponseDTO.class);
                return new SsafyErrorException(errorResponse);
            }
            return new ExternalServerErrorException("범인 -> " + methodKey);
        } catch (IOException e) {
            return new RuntimeException(e.getMessage());
        }
    }

}
