package com.trabean.internal.controller;

import com.trabean.account.dto.response.AccountNoResponseDTO;
import com.trabean.exception.*;
import com.trabean.internal.dto.requestDTO.UserRoleRequestDTO;
import com.trabean.internal.dto.requestDTO.VerifyPasswordRequestDTO;
import com.trabean.internal.dto.responseDTO.UserRoleResponseDTO;
import com.trabean.internal.dto.requestDTO.AccountNoRequestDTO;
import com.trabean.internal.dto.responseDTO.VerifyPasswordResponseDTO;
import com.trabean.internal.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/accounts/internal")
public class InternalController {

    private final InternalService internalService;

    // 통장 계좌번호 조회 API
    @PostMapping("/get-accountNo")
    public ResponseEntity<?> getAccountNo(@RequestBody AccountNoRequestDTO requestDTO) {
        try {
            AccountNoResponseDTO responseDTO = internalService.getAccountNo(requestDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (AccountNotFoundException e) {
            return new ResponseEntity<>(Collections.singletonMap("message", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    // 통장 권한 조회 API
    @PostMapping("/get-userRole")
    public ResponseEntity<?> getUserRole(@RequestBody UserRoleRequestDTO requestDTO) {
        try {
            UserRoleResponseDTO responseDTO = internalService.getUserRole(requestDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (UserAccountRelationNotFoundException e) {
            return new ResponseEntity<>(Collections.singletonMap("message", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    // 결제 비밀번호 검증 API
    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestBody VerifyPasswordRequestDTO requestDTO) {
        try {
            VerifyPasswordResponseDTO responseDTO = internalService.verifyPassword(requestDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (InvalidPasswordException | UnauthorizedTransactionException e) {
            return new ResponseEntity<>(Collections.singletonMap("message", e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (AccountNotFoundException | UserAccountRelationNotFoundException e) {
            return new ResponseEntity<>(Collections.singletonMap("message", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

}
