package com.trabean.account.controller;

import com.trabean.account.dto.request.*;
import com.trabean.account.dto.response.*;
import com.trabean.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    // 개인 통장 생성 API
    @PostMapping("/personal")
    public ResponseEntity<CreatePersonalAccountResponseDTO> createPersonalAccount(@RequestBody CreatePersonalAccountRequestDTO requestDTO){
        CreatePersonalAccountResponseDTO responseDTO = accountService.createPersonalAccount(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    // 한화 여행통장 생성 API
    @PostMapping("/travel/domestic")
    public ResponseEntity<CreateDomesticTravelAccountResponseDTO> createDomesticTravelAccount(@RequestBody CreateDomesticTravelAccountRequestDTO requestDTO){
        CreateDomesticTravelAccountResponseDTO responseDTO = accountService.createDomesticTravelAccount(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    // 통장 목록 조회 API
    @PostMapping
    public ResponseEntity<AccountListResponseDTO> getAccountList(@RequestBody AccountListRequestDTO requestDTO){
        AccountListResponseDTO responseDTO = accountService.getAccountList(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // 통장 계좌번호 조회 API
    @PostMapping("/get-account-number")
    public ResponseEntity<AccountNoResponseDTO> getAccountNo(@RequestBody AccountNoRequestDTO requestDTO){
        AccountNoResponseDTO responseDTO = accountService.getAccountNoById(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // 통장 권환 조회 API
    @PostMapping("/get-user-role")
    public ResponseEntity<UserRoleResponseDTO> getUserRole(@RequestBody UserRoleRequestDTO requestDTO){
        UserRoleResponseDTO responseDTO = accountService.getUserRoleByUserIdAndAccountId(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}
