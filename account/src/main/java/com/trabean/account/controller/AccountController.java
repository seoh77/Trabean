package com.trabean.account.controller;

import com.trabean.account.dto.request.AccountNoRequestDTO;
import com.trabean.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private final AccountService accountService;

    @PostMapping("/get-account-number")
    public ResponseEntity<String> getAccountNo(@RequestBody AccountNoRequestDTO requestDTO){
        Long accountId = requestDTO.getAccountId();
        String accountNo = accountService.getAccountNoById(accountId);

        if(accountNo != null){
            return ResponseEntity.ok(accountNo);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("없는 계좌 입니다.");
        }
    }
}
