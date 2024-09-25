package com.trabean.payment.service;

import com.trabean.payment.dto.response.PaymentsHistoryResponse;
import com.trabean.payment.dto.response.PaymentsHistoryResponse.Data;
import com.trabean.payment.entity.Payments;
import com.trabean.payment.repository.PaymentsRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentsHistoryService {

    private final PaymentsRepository paymentsRepository;

    public PaymentsHistoryResponse getPaymentHistory(Long travelAccountId, LocalDate startdate, LocalDate enddate,
                                                     int page) {
        Pageable pageable = PageRequest.of(page, 20);  // 20개씩 페이지 처리
        Page<Payments> paymentsPage = paymentsRepository.findAllByAccountIdDateRange(
                travelAccountId, startdate, enddate, pageable);

        // 페이지 정보 가져오기
        List<PaymentsHistoryResponse.Data> payments = paymentsPage.getContent().stream()
                .map(this::toPaymentsHistoryData)
                .collect(Collectors.toList());

        // 응답 생성
        return new PaymentsHistoryResponse(
                travelAccountId,
                payments.stream().mapToLong(Data::getKrwAmount).sum(), // 총 금액 계산
                payments,
                new PaymentsHistoryResponse.Pagination(
                        (long) paymentsPage.getNumber() + 1,
                        (long) paymentsPage.getTotalPages(),
                        paymentsPage.getTotalElements()
                )
        );
    }

    // DTO로 변환
    private PaymentsHistoryResponse.Data toPaymentsHistoryData(Payments payment) {

        return new PaymentsHistoryResponse.Data(
                payment.getPayId(),
                payment.getMerchant().getMerchantId(),
                payment.getMerchant().getName(),
                payment.getPaymentDate().toString(),
                payment.getKrwAmount(),
                payment.getForeignAmount(),
                payment.getUserId().toString(), // userName 대신 userId 사용
                payment.getMerchant().getCategory() // 카테고리 필드 예시
        );
    }
}