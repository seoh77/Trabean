package com.trabean.verification.service;

import com.trabean.ssafy.api.account.domestic.client.DomesticClient;
import com.trabean.ssafy.api.account.domestic.dto.requestDTO.InquireDemandDepositAccountRequestDTO;
import com.trabean.ssafy.api.account.domestic.dto.requestDTO.InquireTransactionHistoryRequestDTO;
import com.trabean.ssafy.api.account.domestic.dto.responseDTO.InquireDemandDepositAccountResponseDTO;
import com.trabean.ssafy.api.account.domestic.dto.responseDTO.InquireTransactionHistoryResponseDTO;
import com.trabean.ssafy.api.verification.client.VerificationClient;
import com.trabean.ssafy.api.verification.dto.requestDTO.CheckAuthCodeRequestDTO;
import com.trabean.ssafy.api.verification.dto.requestDTO.OpenAccountAuthRequestDTO;
import com.trabean.ssafy.api.verification.dto.responseDTO.CheckAuthCodeResponseDTO;
import com.trabean.ssafy.api.verification.dto.responseDTO.OpenAccountAuthResponseDTO;
import com.trabean.util.RequestHeader;
import com.trabean.verification.dto.request.AccountVerificationRequestDTO;
import com.trabean.verification.dto.request.OneWonVerificationRequestDTO;
import com.trabean.verification.dto.response.AccountVerificationResponseDTO;
import com.trabean.verification.dto.response.OneWonVerificationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final DomesticClient domesticClient;
    private final VerificationClient verificationClient;

    public AccountVerificationResponseDTO getAccountVerification(AccountVerificationRequestDTO requestDTO){
        String userKey = requestDTO.getUserKey();
        String accountNo = requestDTO.getAccountNo();

        InquireDemandDepositAccountRequestDTO inquireDemandDepositAccountRequestDTO = InquireDemandDepositAccountRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("inquireDemandDepositAccount")
                        .userKey(userKey)
                        .build())
                .accountNo(accountNo)
                .build();

        InquireDemandDepositAccountResponseDTO inquireDemandDepositAccountResponseDTO = domesticClient.inquireDemandDepositAccount(inquireDemandDepositAccountRequestDTO);

        String verifiedAccountNo = inquireDemandDepositAccountResponseDTO.getRec().getAccountNo();

        OpenAccountAuthRequestDTO openAccountAuthRequestDTO = OpenAccountAuthRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("openAccountAuth")
                        .userKey(userKey)
                        .build())
                .accountNo(verifiedAccountNo)
                .build();

        OpenAccountAuthResponseDTO openAccountAuthResponseDTO = verificationClient.openAccountAuth(openAccountAuthRequestDTO);

        Long transactionUniqueNo = openAccountAuthResponseDTO.getRec().getTransactionUniqueNo();

        InquireTransactionHistoryRequestDTO inquireTransactionHistoryRequestDTO = InquireTransactionHistoryRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("inquireTransactionHistory")
                        .userKey(userKey)
                        .build())
                .accountNo(verifiedAccountNo)
                .transactionUniqueNo(transactionUniqueNo)
                .build();

        InquireTransactionHistoryResponseDTO inquireTransactionHistoryResponseDTO = domesticClient.inquireTransactionHistory(inquireTransactionHistoryRequestDTO);

        String message = inquireTransactionHistoryResponseDTO.getHeader().getResponseMessage();

        return AccountVerificationResponseDTO.builder()
                .message(message)
                .build();
    }

    public OneWonVerificationResponseDTO getOneWonVerification(OneWonVerificationRequestDTO requestDTO) {
        String userKey = requestDTO.getUserKey();
        String accountNo = requestDTO.getAccountNo();
        String otp = requestDTO.getOtp();

        CheckAuthCodeRequestDTO checkAuthCodeRequestDTO = CheckAuthCodeRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("checkAuthCode")
                        .userKey(userKey)
                        .build())
                .accountNo(accountNo)
                .authCode(otp)
                .build();

        CheckAuthCodeResponseDTO checkAuthCodeResponseDTO = verificationClient.checkAuthCode(checkAuthCodeRequestDTO);

        String message = checkAuthCodeResponseDTO.getRec().getStatus();

        return OneWonVerificationResponseDTO.builder()
                .message(message)
                .build();
    }
}
