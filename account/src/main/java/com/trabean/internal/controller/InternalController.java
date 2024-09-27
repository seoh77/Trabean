package com.trabean.internal.controller;

import com.trabean.account.dto.response.AccountNoResponseDTO;
import com.trabean.internal.dto.requestDTO.AdminUserKeyRequestDTO;
import com.trabean.internal.dto.requestDTO.UserRoleRequestDTO;
import com.trabean.internal.dto.requestDTO.VerifyPasswordRequestDTO;
import com.trabean.internal.dto.responseDTO.AdminUserKeyResponseDTO;
import com.trabean.internal.dto.responseDTO.UserRoleResponseDTO;
import com.trabean.internal.dto.requestDTO.AccountNoRequestDTO;
import com.trabean.internal.dto.responseDTO.VerifyPasswordResponseDTO;
import com.trabean.internal.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/accounts/internal")
public class InternalController {

    private final InternalService internalService;

    // 통장 계좌번호 조회 API
    @PostMapping("/get-accountNo")
    public ResponseEntity<AccountNoResponseDTO> getAccountNo(@RequestBody AccountNoRequestDTO requestDTO) {
        AccountNoResponseDTO responseDTO = internalService.getAccountNo(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // 통장 권한 조회 API
    @PostMapping("/get-userRole")
    public ResponseEntity<UserRoleResponseDTO> getUserRole(@RequestBody UserRoleRequestDTO requestDTO) {
        UserRoleResponseDTO responseDTO = internalService.getUserRole(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // 결제 비밀번호 검증 API
    @PostMapping("/verify-password")
    public ResponseEntity<VerifyPasswordResponseDTO> verifyPassword(@RequestBody VerifyPasswordRequestDTO requestDTO) {
        VerifyPasswordResponseDTO responseDTO = internalService.verifyPassword(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    //
    @PostMapping("/get-admin-userKey")
    public ResponseEntity<AdminUserKeyResponseDTO> getAdminUserKey(@RequestBody AdminUserKeyRequestDTO requestDTO) {
        AdminUserKeyResponseDTO responseDTO = internalService.getAdminUserKey(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
