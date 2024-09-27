package com.trabean.internal.controller;

import com.trabean.account.dto.response.AccountNoResponseDTO;
import com.trabean.internal.dto.requestDTO.*;
import com.trabean.internal.dto.responseDTO.*;
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

    // 여행통장 결제 권한 변경 API
    @PutMapping("/update-userRole")
    public ResponseEntity<UpdateUserRoleResponseDTO> updateUserRole(@RequestBody UpdateUserRoleRequestDTO requestDTO) {
        UpdateUserRoleResponseDTO responseDTO = internalService.updateUserRole(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // 결제 비밀번호 검증 API
    @PostMapping("/verify-password")
    public ResponseEntity<VerifyPasswordResponseDTO> verifyPassword(@RequestBody VerifyPasswordRequestDTO requestDTO) {
        VerifyPasswordResponseDTO responseDTO = internalService.verifyPassword(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // 여행통장 가입 API
    @PostMapping("join-member")
    public ResponseEntity<JoinTravelAccountResponseDTO> joinTravelAccount(@RequestBody JoinTravelAccountRequestDTO requestDTO) {
        JoinTravelAccountResponseDTO responseDTO = internalService.joinTravelAccount(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // 통장 주인의 userKey 조회 API
    @PostMapping("/get-admin-userKey")
    public ResponseEntity<AdminUserKeyResponseDTO> getAdminUserKey(@RequestBody AdminUserKeyRequestDTO requestDTO) {
        AdminUserKeyResponseDTO responseDTO = internalService.getAdminUserKey(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
