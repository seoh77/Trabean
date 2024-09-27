package com.trabean.verification.service;

import com.trabean.common.ResponseCode;
import com.trabean.common.SsafySuccessResponseDTO;
import com.trabean.external.ssafy.verification.client.VerificationClient;
import com.trabean.external.ssafy.verification.dto.requestDTO.CheckAuthCodeRequestDTO;
import com.trabean.external.ssafy.verification.dto.requestDTO.OpenAccountAuthRequestDTO;
import com.trabean.external.ssafy.verification.dto.responseDTO.CheckAuthCodeResponseDTO;
import com.trabean.external.ssafy.verification.dto.responseDTO.OpenAccountAuthResponseDTO;
import com.trabean.util.RequestHeader;
import com.trabean.verification.dto.request.AccountVerificationRequestDTO;
import com.trabean.verification.dto.request.OneWonVerificationRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationClient verificationClient;

    // 1원 인증(1원 송금) 서비스 로직
    public SsafySuccessResponseDTO getAccountVerification(String userKey, AccountVerificationRequestDTO requestDTO) {

        // SSAFY 금융 API 1원 송금 요청
        OpenAccountAuthRequestDTO openAccountAuthRequestDTO = OpenAccountAuthRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("openAccountAuth")
                        .userKey(userKey)
                        .build())
                .accountNo(requestDTO.getAccountNo())
                .build();

        OpenAccountAuthResponseDTO openAccountAuthResponseDTO = verificationClient.openAccountAuth(openAccountAuthRequestDTO);

        ResponseCode responseCode = openAccountAuthResponseDTO.getHeader().getResponseCode();
        String responseMessage = openAccountAuthResponseDTO.getHeader().getResponseMessage();

        return SsafySuccessResponseDTO.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .build();
    }

    // 1원 인증(인증번호검증) 서비스 로직
    public SsafySuccessResponseDTO getOneWonVerification(String userKey, OneWonVerificationRequestDTO requestDTO) {

        // SSAFY 금융 API 1원 송금 검증 요청
        CheckAuthCodeRequestDTO checkAuthCodeRequestDTO = CheckAuthCodeRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("checkAuthCode")
                        .userKey(userKey)
                        .build())
                .accountNo(requestDTO.getAccountNo())
                .authCode(requestDTO.getOtp())
                .build();

        CheckAuthCodeResponseDTO checkAuthCodeResponseDTO = verificationClient.checkAuthCode(checkAuthCodeRequestDTO);

        ResponseCode responseCode = checkAuthCodeResponseDTO.getHeader().getResponseCode();
        String responseMessage = checkAuthCodeResponseDTO.getHeader().getResponseMessage();

        return SsafySuccessResponseDTO.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .build();
    }

}
