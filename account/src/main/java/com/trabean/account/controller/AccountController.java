package com.trabean.account.controller;

import com.trabean.account.dto.request.AccountNoRequestDTO;
import com.trabean.account.dto.request.UserRoleRequestDTO;
import com.trabean.account.dto.response.AccountNoResponseDTO;
import com.trabean.account.dto.response.UserRoleResponseDTO;
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
    public ResponseEntity<AccountNoResponseDTO> getAccountNo(@RequestBody AccountNoRequestDTO requestDTO){
        Long accountId = requestDTO.getAccountId();

        AccountNoResponseDTO responseDTO = accountService.getAccountNoById(accountId);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/get-user-role")
    public ResponseEntity<UserRoleResponseDTO> getUserRole(@RequestBody UserRoleRequestDTO requestDTO){
        Long userId = requestDTO.getUserId();
        Long accountId = requestDTO.getAccountId();

        UserRoleResponseDTO responseDTO = accountService.getUserRoleByUserIdAndAccountId(userId, accountId);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
