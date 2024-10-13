package com.trabean.payment.service;

import com.trabean.payment.client.ssafy.DemandDepositClient;
import com.trabean.payment.dto.request.Header;
import com.trabean.payment.dto.request.RequestPaymentRequest;
import com.trabean.payment.dto.request.WithdrawalRequest;
import com.trabean.payment.dto.response.WithdrawalResponse;
import com.trabean.payment.entity.Merchants;
import com.trabean.payment.exception.PaymentsException;
import com.trabean.payment.repository.MerchantsRepository;
import com.trabean.payment.util.ApiName;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentsWithdrawalService {

    private final PaymentsAccountService paymentAccountService;
    private final PaymentsAccountService paymentsAccountService;
    private final DemandDepositClient demandDepositClient;
    private static final Logger logger = LoggerFactory.getLogger(PaymentsWithdrawalService.class);
    private final MerchantsRepository merchantsRepository;


    public void withdrawalToPay(RequestPaymentRequest request, Long accountId, String apiType) {
        // 유저 키 조회
        String userKey = paymentAccountService.getAccountAdmin(accountId);

        // 계좌 정보 조회
        String accountNo = paymentsAccountService.getAccountNumber(accountId);
        Long price = apiType.equals(ApiName.KRW_WITHDRAW) ? request.getKrwAmount()
                : (long) request.getForeignAmount().doubleValue();
        logger.info("출금 중 오류 발생 : " + request.getKrwAmount());

        Merchants merchant = merchantsRepository.findById(request.getMerchantId())
                .orElseThrow(() -> new PaymentsException("결제할 가맹점 정보를 찾지 못했습니다.", HttpStatus.BAD_REQUEST));

        WithdrawalRequest withdrawalRequest = new WithdrawalRequest(
                Header.builder().apiName(apiType).userKey(userKey).build(),
                accountNo, price, merchant.getName()
        );

        WithdrawalResponse response;
        // API 호출
        if (apiType.equals(ApiName.KRW_WITHDRAW)) {
            response = demandDepositClient.withdrawKRW(withdrawalRequest);
        } else { // 외화일 때
            response = demandDepositClient.withdrawFOR(withdrawalRequest);
        }

        // 결과 로그 남기기
        if (response != null && response.getRec() != null) {
            logger.info("출금 성공: {}", response.getRec().getTransactionDate());
        } else {
            logger.warn("출금 return 값이 null 입니다.");
        }

    }
}
