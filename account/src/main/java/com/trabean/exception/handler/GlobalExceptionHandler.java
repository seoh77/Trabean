package com.trabean.exception;

import com.trabean.exception.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {
            AccountNotFoundException.class,
            UserAccountRelationNotFoundException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(RuntimeException e) {
        ErrorResponseDTO responseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {
            InvalidAccountTypeException.class,
            InvalidPasswordException.class,
            UnauthorizedUserRoleException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleUForbiddenException(RuntimeException e) {
        ErrorResponseDTO responseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.FORBIDDEN);
    }

}
