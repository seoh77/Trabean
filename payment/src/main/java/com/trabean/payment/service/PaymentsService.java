package com.trabean.payment.service;

import com.trabean.payment.client.NotificationClient;
import com.trabean.payment.dto.request.NotificationRequest;
import com.trabean.payment.dto.request.RequestPaymentRequest;
import com.trabean.payment.dto.response.PaymentResponse;
import com.trabean.payment.dto.response.TravelAccountMemberListResponse;
import com.trabean.payment.dto.response.TravelAccountMemberListResponse.Members;
import com.trabean.payment.entity.Payments;
import com.trabean.payment.exception.PaymentsException;
import com.trabean.payment.interceptor.UserHeaderInterceptor;
import com.trabean.payment.repository.PaymentsRepository;
import com.trabean.payment.util.ApiName;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentsService {

    private final PaymentsAuthService paymentsAuthService;
    private final PaymentsValidateService paymentsValidateService;
    private final PaymentsAccountService paymentsAccountService;
    private final PaymentsWithdrawalService paymentsWithdrawalService;
    private final PaymentsUpdateInfoService paymentsUpdateInfoService;
    private final PaymentsRepository paymentsRepository;
    private final NotificationClient notificationClient;

    // 공통된 결제 요청 검증 메서드
    private void validateRequestPayment(Long accountId, RequestPaymentRequest request) {
        // 결제 권한 검증
        log.info("유저 아이디 가져오기" + UserHeaderInterceptor.userId.get());
        paymentsAuthService.checkAuthPayment(UserHeaderInterceptor.userId.get(), accountId);

        // transactionId 검증
        paymentsValidateService.validateTransactionId(request.getTransactionId(), request.getPayId());

        // 데이터 무결성 검증
        if (request.getForeignAmount() != null) {
            paymentsValidateService.validatePaymentData(request.getPayId(), request.getMerchantId(),
                    request.getForeignAmount(), null);
        } else {
            paymentsValidateService.validatePaymentData(request.getPayId(), request.getMerchantId(), null,
                    request.getKrwAmount());
        }
    }

    // 결제 요청 처리 메서드
    public PaymentResponse requestPayment(Long accountId, RequestPaymentRequest request) {
        // 결제 요청 검증
        validateRequestPayment(accountId, request);

        // 외화 결제 여부를 확인
        if (request.getForeignAmount() != null) {
            paymentsAccountService.getFORAccount(request.getMerchantId(), accountId);
            return processForeignPayment(accountId, request);
        } else {
            // 재결제 처리
            return processKrwPayment(accountId, request);
        }
    }

    // 외화 결제 처리
    public PaymentResponse processForeignPayment(Long accountId, RequestPaymentRequest request) {
        // 외화 계좌 조회
        Long paymentAccountId = paymentsAccountService.getFORAccount(request.getMerchantId(), accountId);
        log.info("외화계좌조회@@@@@@@@" + paymentAccountId);
        if (paymentAccountId == null) {
            log.info(request.getPayId() + "결제 id 찾았나???@@@@@@@@");
            Payments payment = paymentsRepository.findById(request.getPayId())
                    .orElseThrow(() -> new PaymentsException("결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
            log.info("결제정보는 찾았네");
            throw new PaymentsException("FOREIGN_ACCOUNT_NOT_FOUND", payment.getKrwAmount(), HttpStatus.NOT_FOUND);
        }

        // 외화 결제 로직 처리
        return executePayment(paymentAccountId, request, ApiName.FOREIGN_WITHDRAW);
    }

    // 한화 결제 처리
    public PaymentResponse processKrwPayment(Long accountId, RequestPaymentRequest request) {
        // 한화 결제 로직 처리
        return executePayment(accountId, request, ApiName.KRW_WITHDRAW);
    }

    // 공통 결제 처리 로직
    private PaymentResponse executePayment(Long paymentAccountId, RequestPaymentRequest request,
                                           String payApiType) {
        // 잔액 검증
        if (payApiType.equals(ApiName.KRW_WITHDRAW)) {
            paymentsAccountService.validateKrwAmount(paymentAccountId, request);
        } else {
            paymentsAccountService.validateForeignAmount(paymentAccountId, request);
        }

        // 출금 처리
        paymentsWithdrawalService.withdrawalToPay(request, paymentAccountId, payApiType);

        // 결제 상태를 성공으로 업데이트
        paymentsUpdateInfoService.updateSuccess(request.getPayId());

        // 결제정보
        Payments payments = paymentsRepository.findById(request.getPayId())
                .orElseThrow(() -> new PaymentsException("결제정보 받아올수없습니다.", HttpStatus.INTERNAL_SERVER_ERROR));

        // 여행통장 멤버 조회
        TravelAccountMemberListResponse memberListResponse = paymentsAccountService.validateTravelAccountMembers(
                payments.getAccountId());

        // 여행 통장 멤버 list 에 담기
        List<Long> membersId = new ArrayList<>();
        for (Members members : memberListResponse.getMembers()) {
            membersId.add(members.getUserId());
        }

        // 결제 알림 발송
        NotificationRequest notificationRequest = NotificationRequest.builder().
                senderId(UserHeaderInterceptor.userId.get())
                .receiverId(membersId)
                .accountId(paymentAccountId)
                .notificationType("PAYMENT")
                .amount(request.getForeignAmount() != null ? Math.round(request.getForeignAmount())
                        : request.getKrwAmount())
                .build();
        notificationClient.sendNotification(notificationRequest);

        // 응답 생성
        PaymentResponse.PaymentInfo paymentInfo = PaymentResponse.PaymentInfo.builder()
                .krwAmount(request.getKrwAmount())
                .foreignAmount(request.getForeignAmount())
                .build();

        return PaymentResponse.builder()
                .status("SUCCESS")
                .message("결제에 성공했습니다.")
                .paymentInfo(paymentInfo)
                .build();
    }
}
