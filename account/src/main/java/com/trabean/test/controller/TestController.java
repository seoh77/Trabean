package com.trabean.test.controller;

import com.trabean.external.ssafy.common.SsafyApiResponseDTO;
import com.trabean.test.dto.request.DepositRequestDTO;
import com.trabean.test.dto.request.WithdrawalRequestDTO;
import com.trabean.test.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/accounts/test")
public class TestController {

    private final TestService testService;

    // 계좌 입금(테스트용) API
    @PostMapping("/deposit")
    ResponseEntity<SsafyApiResponseDTO> depositTest(@RequestBody DepositRequestDTO requestDTO) {
        SsafyApiResponseDTO responseDTO = testService.depositTest(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // 계좌 출금(테스트용) API
    @PostMapping("/withdrawal")
    ResponseEntity<SsafyApiResponseDTO> withdrawalTest(@RequestBody WithdrawalRequestDTO requestDTO) {
        SsafyApiResponseDTO responseDTO = testService.withdrawalTest(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
