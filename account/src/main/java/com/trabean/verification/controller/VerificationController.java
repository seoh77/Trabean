package com.trabean.verification.controller;

import com.trabean.verification.dto.request.AccountVerificationRequestDTO;
import com.trabean.verification.dto.request.OneWonVerificationRequestDTO;
import com.trabean.verification.dto.response.AccountVerificationResponseDTO;
import com.trabean.verification.dto.response.OneWonVerificationResponseDTO;
import com.trabean.verification.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/accounts/verification")
public class VerificationController {

    private final VerificationService verificationService;

    // 1원 인증(1원 송금) API
    @PostMapping("/account")
    public ResponseEntity<AccountVerificationResponseDTO> getAccountVerification(@RequestHeader Long userId,
                                                                                 @RequestHeader String userKey,
                                                                                 @RequestBody AccountVerificationRequestDTO requestDTO) {
        AccountVerificationResponseDTO responseDTO = verificationService.getAccountVerification(userId, userKey, requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // 1원 인증(인증번호검증) API
    @PostMapping("/onewon")
    public ResponseEntity<OneWonVerificationResponseDTO> getOneWonVerification(@RequestHeader String userKey,
                                                                               @RequestBody OneWonVerificationRequestDTO requestDTO) {
        OneWonVerificationResponseDTO responseDTO = verificationService.getOneWonVerification(userKey, requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
