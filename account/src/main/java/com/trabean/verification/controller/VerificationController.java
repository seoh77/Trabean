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

    @PostMapping("/account")
    public ResponseEntity<AccountVerificationResponseDTO> getAccountVerification(@RequestBody AccountVerificationRequestDTO requestDTO){
        AccountVerificationResponseDTO responseDTO = verificationService.getAccountVerification(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/onewon")
    public ResponseEntity<OneWonVerificationResponseDTO> getOneWonVerification(@RequestBody OneWonVerificationRequestDTO requestDTO){
        OneWonVerificationResponseDTO responseDTO = verificationService.getOneWonVerification(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
