package com.trabean.exception.handler;

import com.trabean.common.ExternalServerErrorResponseDTO;
import com.trabean.common.InternalServerErrorResponseDTO;
import com.trabean.common.ResponseCode;
import com.trabean.common.SsafyErrorResponseDTO;
import com.trabean.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 전역 예외 처리 핸들러
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Account 서버에서 404 에러코드를 응답하는 상황 (DB 조회 실패)
     */
    @ExceptionHandler(value = {
            AccountNotFoundException.class,
            UserAccountRelationNotFoundException.class
    })
    public ResponseEntity<InternalServerErrorResponseDTO> handleNotFoundException(InternalServerErrorResponseDTO e) {
        InternalServerErrorResponseDTO responseDTO = InternalServerErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
    }

    /**
     * Account 서버에서 401 에러코드를 응답하는 상황 (요청 인가 실패)
     */
    @ExceptionHandler(value = {
            InvalidAccountTypeException.class,
            InvalidPasswordException.class,
            UnauthorizedUserRoleException.class
    })
    public ResponseEntity<InternalServerErrorResponseDTO> handleUForbiddenException(InternalServerErrorResponseDTO e) {
        InternalServerErrorResponseDTO responseDTO = InternalServerErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.FORBIDDEN);
    }

    /**
     * Account 서버에서 500 에러코드를 응답하는 상황 (서버 내부 에러)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<InternalServerErrorResponseDTO> handleInternalServerException(RuntimeException e) {
        InternalServerErrorResponseDTO responseDTO = InternalServerErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 다른 서버에서 에러 응답을 보내는 상황
     */
    @ExceptionHandler(ExternalServerErrorException.class)
    public ResponseEntity<ExternalServerErrorResponseDTO> handleExternalServerException(ExternalServerErrorException e) {
        ExternalServerErrorResponseDTO responseDTO = ExternalServerErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_GATEWAY);
    }

    /**
     * SSAFY 금융 API 에서 에러 응답을 보내는 상황
     */
    @ExceptionHandler(SsafyErrorException.class)
    public ResponseEntity<SsafyErrorResponseDTO> handleCustomFeignClientException(SsafyErrorException e) {
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
