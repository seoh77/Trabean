package com.trabean.test;

import com.trabean.account.dto.response.AccountNoResponseDTO;
import com.trabean.exception.AccountNotFoundException;
import com.trabean.internal.dto.requestDTO.AccountNoRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/accounts/test")
public class TestController {

    // 통장 계좌번호 조회 API
//    @PostMapping("/get-accountNo")
//    public ResponseEntity<?> getAccountNo(@RequestBody AccountNoRequestDTO requestDTO) {
//        try {
//            AccountNoResponseDTO responseDTO = internalService.getAccountNo(requestDTO);
//            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
//        } catch (AccountNotFoundException e) {
//            return new ResponseEntity<>(Collections.singletonMap("message", e.getMessage()), HttpStatus.NOT_FOUND);
//        }
//    }

    //
    @GetMapping()
    public ResponseEntity<?> test() {
        TestResponseDTO testResponseDTO = new TestResponseDTO();
        testResponseDTO.setText("아아아아아아제발");

        return ResponseEntity.ok().body(testResponseDTO);
    }
    //
}
