package com.trabean.verification.service;

import com.trabean.account.repository.AccountRepository;
import com.trabean.common.SsafySuccessResponseDTO;
import com.trabean.external.msa.notification.client.NotificationClient;
import com.trabean.external.msa.notification.dto.request.NotificationRequestDTO;
import com.trabean.external.ssafy.verification.client.VerificationClient;
import com.trabean.external.ssafy.verification.dto.request.CheckAuthCodeRequestDTO;
import com.trabean.external.ssafy.verification.dto.request.OpenAccountAuthRequestDTO;
import com.trabean.external.ssafy.verification.dto.response.CheckAuthCodeResponseDTO;
import com.trabean.external.ssafy.verification.dto.response.OpenAccountAuthResponseDTO;
import com.trabean.interceptor.UserHeaderInterceptor;
import com.trabean.util.RequestHeader;
import com.trabean.util.ValidationUtil;
import com.trabean.verification.dto.request.AccountVerificationRequestDTO;
import com.trabean.verification.dto.request.OneWonVerificationRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.trabean.external.msa.notification.dto.request.NotificationRequestDTO.Type.DEPOSIT;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final AccountRepository accountRepository;

    private final VerificationClient verificationClient;

    private final NotificationClient notificationClient;

    // 1원 인증(1원 송금) 서비스 로직
    public SsafySuccessResponseDTO getAccountVerification(AccountVerificationRequestDTO requestDTO) {

        // SSAFY 금융 API 1원 송금 요청
        OpenAccountAuthRequestDTO openAccountAuthRequestDTO = OpenAccountAuthRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("openAccountAuth")
                        .userKey(UserHeaderInterceptor.userKey.get())
                        .build())
                .accountNo(requestDTO.getAccountNo())
                .build();
        OpenAccountAuthResponseDTO openAccountAuthResponseDTO = verificationClient.openAccountAuth(openAccountAuthRequestDTO);

        Long accountId = ValidationUtil.validateAccount(accountRepository.findByAccountNo(requestDTO.getAccountNo())).getAccountId();

        NotificationRequestDTO notificationRequestDTO = NotificationRequestDTO.builder()
                    .senderId(-1L)
                    .receiverIdList(List.of(UserHeaderInterceptor.userId.get()))
                    .accountId(accountId)
                    .notificationType(DEPOSIT)
                    .amount(1L)
                .build();

        // Notification 서버 입출금 시 알림 생성 요청
        notificationClient.sendNotification(notificationRequestDTO);

        return SsafySuccessResponseDTO.builder()
                .responseCode(openAccountAuthResponseDTO.getHeader().getResponseCode())
                .responseMessage(openAccountAuthResponseDTO.getHeader().getResponseMessage())
                .build();
    }

    // 1원 인증(인증번호검증) 서비스 로직
    public SsafySuccessResponseDTO getOneWonVerification(OneWonVerificationRequestDTO requestDTO) {

        // SSAFY 금융 API 1원 송금 검증 요청
        CheckAuthCodeRequestDTO checkAuthCodeRequestDTO = CheckAuthCodeRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName("checkAuthCode")
                        .userKey(UserHeaderInterceptor.userKey.get())
                        .build())
                .accountNo(requestDTO.getAccountNo())
                .authCode(requestDTO.getOtp())
                .build();
        CheckAuthCodeResponseDTO checkAuthCodeResponseDTO = verificationClient.checkAuthCode(checkAuthCodeRequestDTO);

        return SsafySuccessResponseDTO.builder()
                .responseCode(checkAuthCodeResponseDTO.getHeader().getResponseCode())
                .responseMessage(checkAuthCodeResponseDTO.getHeader().getResponseMessage())
                .build();
    }

}
