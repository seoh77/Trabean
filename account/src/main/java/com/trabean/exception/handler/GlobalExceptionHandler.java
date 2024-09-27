package com.trabean.exception.handler;

import com.trabean.common.ResponseCode;
import com.trabean.exception.*;
import com.trabean.exception.dto.ExternalErrorResponseDTO;
import com.trabean.exception.dto.InternalErrorResponseDTO;
import com.trabean.exception.dto.SsafyErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 내 서버에서 404 에러코드를 응답하는 상황
     */
    @ExceptionHandler(value = {
            AccountNotFoundException.class,
            UserAccountRelationNotFoundException.class
    })
    public ResponseEntity<InternalErrorResponseDTO> handleNotFoundException(RuntimeException e) {
        InternalErrorResponseDTO responseDTO = InternalErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
    }

    /**
     * 내 서버에서 401 에러코드를 응답하는 상황
     */
    @ExceptionHandler(value = {
            InvalidAccountTypeException.class,
            InvalidPasswordException.class,
            UnauthorizedUserRoleException.class
    })
    public ResponseEntity<InternalErrorResponseDTO> handleUForbiddenException(RuntimeException e) {
        InternalErrorResponseDTO responseDTO = InternalErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.FORBIDDEN);
    }

    /**
     * 다른 서버에서 에러 응답을 보내는 상황
     */
    @ExceptionHandler(InternalServerStatusException.class)
    public ResponseEntity<ExternalErrorResponseDTO> handleInternalServerException(RuntimeException e) {
        ExternalErrorResponseDTO responseDTO = ExternalErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_GATEWAY);
    }

    /**
     * SSAFY 금융 API에서 에러 응답을 보내는 상황
     */
    @ExceptionHandler(CustomFeignClientException.class)
    public ResponseEntity<SsafyErrorResponseDTO> handleCustomFeignClientException(CustomFeignClientException e) {
        SsafyErrorResponseDTO responseDTO = SsafyErrorResponseDTO.builder()
                .responseCode(e.getErrorResponse().getResponseCode())
                .responseMessage(e.getErrorResponse().getResponseMessage())
                .build();

        ResponseCode responseCode = e.getErrorResponse().getResponseCode();

        switch (responseCode) {
            case H0000 -> {
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
            }
            case A1003, A1086, A1087, A1088, A1090 -> {
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            default -> {
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }
        }
    }
}
