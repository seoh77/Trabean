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
    public ResponseEntity<CreatePersonalAccountResponseDTO> createPersonalAccount(@RequestHeader Long userId,
                                                                                  @RequestHeader String userKey,
                                                                                  @RequestBody CreatePersonalAccountRequestDTO requestDTO) {
        CreatePersonalAccountResponseDTO responseDTO = accountService.createPersonalAccount(userId, userKey, requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    // 한화 여행통장 생성 API
    @PostMapping("/travel/domestic")
    public ResponseEntity<CreateDomesticTravelAccountResponseDTO> createDomesticTravelAccount(@RequestHeader Long userId,
                                                                                              @RequestHeader String userKey,
                                                                                              @RequestBody CreateDomesticTravelAccountRequestDTO requestDTO) {
        CreateDomesticTravelAccountResponseDTO responseDTO = accountService.createDomesticTravelAccount(userId, userKey, requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

//    // 통장 목록 조회 API
//    @PostMapping
//    public ResponseEntity<AccountListResponseDTO> getAccountList(@RequestBody AccountListRequestDTO requestDTO) {
//        AccountListResponseDTO responseDTO = accountService.getAccountList(requestDTO);
//        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
//    }

//    // 개인 통장 상세 조회 API
//    @GetMapping("/personal/{accountId}")
//    public ResponseEntity<PersonalAccountDetailResponseDTO> getAccountDetail(@RequestHeader Long userId,
//                                                                             @RequestHeader String userKey,
//                                                                             @PathVariable Long accountId,
//                                                                             @RequestParam(defaultValue = "19000101") String startDate,
//                                                                             @RequestParam(defaultValue = "21000101") String endDate,
//                                                                             @RequestParam(defaultValue = "A") String transactionType) {
//        PersonalAccountDetailResponseDTO responseDTO = accountService.getAccountDetail(userId, userKey, accountId, startDate, endDate, transactionType);
//        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
//    }

//    // 한화 여행통장 상세 조회 API
//    @PostMapping("/travel/domestic")
//    public ResponseEntity<DomesticTravelAccountDetailResponseDTO> getDomesticTravelAccountDetail(@RequestParam(defaultValue = "19000101") String startDate,
//                                                                     @RequestParam(defaultValue = "21000101") String endDate,
//                                                                     @RequestParam(defaultValue = "A") String transactionType,
//                                                                     @RequestBody DomesticTravelAccountDetailRequestDTO requestDTO){
//        DomesticTravelAccountDetailResponseDTO responseDTO = accountService.getDomesticTravelAccountDetail(requestDTO, startDate, endDate, transactionType);
//        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
//    }

//    // 통장 권한 변경 API
//    @PostMapping("/update-user-role")
//    public ResponseEntity<?> updateUserRole(@RequestBody UpdateUserRoleRequestDTO requestDTO) {
//        UpdateUserRoleResponseDTO responseDTO = accountService.updateUserRole(requestDTO);
//        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
//    }

}
