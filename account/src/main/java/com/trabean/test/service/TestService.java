package com.trabean.test.service;

import com.trabean.external.ssafy.api.domestic.client.DomesticClient;
import com.trabean.external.ssafy.api.domestic.dto.request.UpdateDemandDepositAccountDepositRequestDTO;
import com.trabean.external.ssafy.api.domestic.dto.request.UpdateDemandDepositAccountWithdrawalRequestDTO;
import com.trabean.external.ssafy.api.domestic.dto.response.UpdateDemandDepositAccountDepositResponseDTO;
import com.trabean.external.ssafy.api.domestic.dto.response.UpdateDemandDepositAccountWithdrawalResponseDTO;
import com.trabean.external.ssafy.common.SsafyApiResponseDTO;
import com.trabean.external.ssafy.common.SsafyApiResponseDTOFactory;
import com.trabean.interceptor.UserHeaderInterceptor;
import com.trabean.test.dto.request.DepositRequestDTO;
import com.trabean.test.dto.request.WithdrawalRequestDTO;
import com.trabean.external.ssafy.util.RequestHeader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.trabean.external.ssafy.constant.ApiName.*;

@Service
@RequiredArgsConstructor
public class TestService {

    private final DomesticClient domesticClient;

    // 계좌 입금(테스트용) 서비스 로직
    public SsafyApiResponseDTO depositTest(DepositRequestDTO requestDTO) {

        // SSAFY 금융 API 계좌 입금 요청
        UpdateDemandDepositAccountDepositRequestDTO updateDemandDepositAccountDepositRequestDTO = UpdateDemandDepositAccountDepositRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(updateDemandDepositAccountDeposit)
                        .userKey(UserHeaderInterceptor.userKey.get())
                        .build())
                .accountNo(requestDTO.getAccountNo())
                .transactionBalance(requestDTO.getTransactionBalance())
                .transactionSummary(requestDTO.getTransactionSummary())
                .build();
        UpdateDemandDepositAccountDepositResponseDTO updateDemandDepositAccountDepositResponseDTO = domesticClient.updateDemandDepositAccountDeposit(updateDemandDepositAccountDepositRequestDTO);

        return SsafyApiResponseDTOFactory.create(updateDemandDepositAccountDepositResponseDTO.getHeader());
    }

    // 계좌 출금(테스트용) 서비스 로직
    public SsafyApiResponseDTO withdrawalTest(WithdrawalRequestDTO requestDTO) {

        // SSAFY 금융 API 계좌 출금 요청
        UpdateDemandDepositAccountWithdrawalRequestDTO updateDemandDepositAccountWithdrawalRequestDTO = UpdateDemandDepositAccountWithdrawalRequestDTO.builder()
                .header(RequestHeader.builder()
                        .apiName(updateDemandDepositAccountWithdrawal)
                        .userKey(UserHeaderInterceptor.userKey.get())
                        .build())
                .accountNo(requestDTO.getAccountNo())
                .transactionBalance(requestDTO.getTransactionBalance())
                .transactionSummary(requestDTO.getTransactionSummary())
                .build();
        UpdateDemandDepositAccountWithdrawalResponseDTO updateDemandDepositAccountWithdrawalResponseDTO = domesticClient.updateDemandDepositAccountWithdrawal(updateDemandDepositAccountWithdrawalRequestDTO);

        return SsafyApiResponseDTOFactory.create(updateDemandDepositAccountWithdrawalResponseDTO.getHeader());
    }

}
