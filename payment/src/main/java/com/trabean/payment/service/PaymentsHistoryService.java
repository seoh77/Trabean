package com.trabean.payment.service;

import com.trabean.payment.dto.response.ChartResponse;
import com.trabean.payment.dto.response.PaymentsHistoryCategoryResponse;
import com.trabean.payment.dto.response.PaymentsHistoryResponse;
import com.trabean.payment.dto.response.PaymentsHistoryResponse.Data;
import com.trabean.payment.entity.Payments;
import com.trabean.payment.enums.MerchantCategory;
import com.trabean.payment.exception.PaymentsException;
import com.trabean.payment.repository.CategorySummary;
import com.trabean.payment.repository.PaymentsRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentsHistoryService {

    private final PaymentsRepository paymentsRepository;

    public PaymentsHistoryResponse getPaymentHistory(Long travelAccountId, LocalDate startdate, LocalDate enddate,
                                                     int page) {

        // startdate가 null이면 과거 무한대값으로 설정
        if (startdate == null) {
            startdate = LocalDate.of(1970, 1, 1);
        }

        // enddate가 null이면 오늘 날짜로 설정
        if (enddate == null) {
            enddate = LocalDate.now();
        }

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
                payment.getUserId(), // userName 대신 userId 사용
                payment.getMerchant().getCategory() // 카테고리 필드 예시
        );
    }

    public ChartResponse getChart(Long travelAccountId, LocalDate startdate, LocalDate enddate) {
        // startdate가 null이면 과거 무한대값으로 설정
        if (startdate == null) {
            startdate = LocalDate.of(1970, 1, 1);
        }

        // enddate가 null이면 오늘 날짜로 설정
        if (enddate == null) {
            enddate = LocalDate.now();
        }

        // 카테고리별 총 결제 금액 조회
        List<CategorySummary> categorySummaries = paymentsRepository.findCategorySummaryByAccountIdAndDateRange(
                travelAccountId, startdate, enddate);

        // 전체 결제 금액 계산
        long totalAmount = categorySummaries.stream().mapToLong(CategorySummary::getTotalAmount).sum();

        // 카테고리별 총 금액과 비율 계산
        List<ChartResponse.Category> categoryList = categorySummaries.stream()
                .map(summary -> new ChartResponse.Category(
                        summary.getCategory(),
                        summary.getTotalAmount(),
                        calculatePercent(summary.getTotalAmount(), totalAmount)))
                .collect(Collectors.toList());

        // 응답 DTO 생성
        return new ChartResponse(totalAmount, categoryList);
    }

    private double calculatePercent(Long categoryAmount, Long totalAmount) {
        return totalAmount > 0 ? Math.round((categoryAmount * 100.0 / totalAmount) * 100) / 100.0 : 0;
    }

    public PaymentsHistoryCategoryResponse getPaymentsByCategoryName(Long accountId, String categoryName,
                                                                     LocalDate startDate, LocalDate endDate, int page) {
        // startDate 가 null 이면 과거 무한대값으로 설정
        if (startDate == null) {
            startDate = LocalDate.of(1970, 1, 1);
        }

        // endDate 가 null 이면 오늘 날짜로 설정
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        MerchantCategory categoryEnum;
        try {
            categoryEnum = MerchantCategory.valueOf(categoryName);
        } catch (IllegalArgumentException e) {
            throw new PaymentsException("올바르지 않은 카테고리 이름입니다. 전송된 카테고리 이름: " + categoryName, HttpStatus.BAD_REQUEST);
        }

        // 페이지 처리
        Pageable pageable = PageRequest.of(page, 20);  // 한 페이지에 20개의 결과 반환
        Page<com.trabean.payment.entity.Payments> paymentsPage = paymentsRepository.findAllByCategoryAndDateRange(
                accountId, startDate, endDate,
                categoryEnum, pageable);

        // 전체 결제 금액 계산
        Long categoryTotalAmount = paymentsPage.getContent().stream()
                .mapToLong(com.trabean.payment.entity.Payments::getKrwAmount)
                .sum();

        // 결제 내역 리스트로 변환
        List<PaymentsHistoryCategoryResponse.Payments> payments = paymentsPage.getContent().stream()
                .map(payment -> new PaymentsHistoryCategoryResponse.Payments(
                        payment.getPayId(),
                        payment.getMerchant().getMerchantId(),
                        payment.getMerchant().getName(),
                        payment.getPaymentDate().toString(),
                        payment.getKrwAmount(),
                        payment.getForeignAmount(),
                        payment.getUserId()
                )).collect(Collectors.toList());

        // 페이지 정보 생성
        PaymentsHistoryCategoryResponse.Pagination pagination = new PaymentsHistoryCategoryResponse.Pagination(
                (long) paymentsPage.getNumber() + 1,  // 1부터 시작
                (long) paymentsPage.getTotalPages(),
                paymentsPage.getTotalElements()
        );

        // 응답 DTO 생성 및 반환
        return new PaymentsHistoryCategoryResponse(
                MerchantCategory.valueOf(categoryName),
                categoryTotalAmount,
                payments,
                List.of(pagination)
        );
    }
}