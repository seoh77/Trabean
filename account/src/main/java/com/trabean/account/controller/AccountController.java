package com.trabean.account.controller;

import com.trabean.account.dto.request.AccountNoRequestDTO;
import com.trabean.account.dto.request.UserRoleRequestDTO;
import com.trabean.account.dto.response.AccountNoResponseDTO;
import com.trabean.account.dto.response.UserRoleResponseDTO;
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

    @PostMapping("/get-account-number")
    public ResponseEntity<AccountNoResponseDTO> getAccountNo(@RequestBody AccountNoRequestDTO requestDTO){
        AccountNoResponseDTO responseDTO = accountService.getAccountNoById(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/get-user-role")
    public ResponseEntity<UserRoleResponseDTO> getUserRole(@RequestBody UserRoleRequestDTO requestDTO){
        UserRoleResponseDTO responseDTO = accountService.getUserRoleByUserIdAndAccountId(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
