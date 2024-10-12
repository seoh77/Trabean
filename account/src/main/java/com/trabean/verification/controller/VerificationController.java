package com.trabean.verification.controller;

import com.trabean.external.ssafy.common.SsafyApiResponseDTO;
import com.trabean.verification.dto.request.AccountVerificationRequestDTO;
import com.trabean.verification.dto.request.OneWonVerificationRequestDTO;
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
    public ResponseEntity<SsafyApiResponseDTO> getAccountVerification(@RequestBody AccountVerificationRequestDTO requestDTO) {
        SsafyApiResponseDTO responseDTO = verificationService.getAccountVerification(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // 1원 인증(인증번호검증) API
    @PostMapping("/onewon")
    public ResponseEntity<SsafyApiResponseDTO> getOneWonVerification(@RequestBody OneWonVerificationRequestDTO requestDTO) {
        SsafyApiResponseDTO responseDTO = verificationService.getOneWonVerification(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
