package com.trabean.verification.service;

import com.trabean.account.repository.UserAccountRelationRepository;
import com.trabean.common.ResponseCode;
import com.trabean.exception.UserAccountRelationNotFoundException;
import com.trabean.external.ssafy.verification.client.VerificationClient;
import com.trabean.external.ssafy.verification.dto.requestDTO.CheckAuthCodeRequestDTO;
import com.trabean.external.ssafy.verification.dto.requestDTO.OpenAccountAuthRequestDTO;
import com.trabean.external.ssafy.verification.dto.responseDTO.CheckAuthCodeResponseDTO;
import com.trabean.external.ssafy.verification.dto.responseDTO.OpenAccountAuthResponseDTO;
import com.trabean.util.RequestHeader;
import com.trabean.verification.dto.request.AccountVerificationRequestDTO;
import com.trabean.verification.dto.request.OneWonVerificationRequestDTO;
import com.trabean.verification.dto.response.AccountVerificationResponseDTO;
import com.trabean.verification.dto.response.OneWonVerificationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationClient verificationClient;
    private final UserAccountRelationRepository userAccountRelationRepository;

    // 1원 인증(1원 송금) 서비스 로직
    public AccountVerificationResponseDTO getAccountVerification(Long userId, String userKey, AccountVerificationRequestDTO requestDTO) {

        // 유저가 해당 계좌와 관계가 있는지 확인
        userAccountRelationRepository.findByUserIdAndAccountId(userId, requestDTO.getAccountId())
                .orElseThrow(UserAccountRelationNotFoundException::getInstance);

        // SSAFY API 1원 송금 요청
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

        return AccountVerificationResponseDTO.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .build();
    }

    // 1원 인증(인증번호검증) 서비스 로직
    public OneWonVerificationResponseDTO getOneWonVerification(String userKey, OneWonVerificationRequestDTO requestDTO) {

        // SSAFY API 1원 송금 검증 요청
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

        return OneWonVerificationResponseDTO.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .build();
    }

}
