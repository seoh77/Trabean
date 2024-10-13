package com.trabean.payment.service;

import com.trabean.payment.dto.response.ChartResponse;
import com.trabean.payment.dto.response.PaymentsHistoryCategoryResponse;
import com.trabean.payment.dto.response.PaymentsHistoryResponse;
import com.trabean.payment.entity.Payments;
import com.trabean.payment.enums.MerchantCategory;
import com.trabean.payment.exception.PaymentsException;
import com.trabean.payment.repository.CategorySummary;
import com.trabean.payment.repository.PaymentsRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentsHistoryService {

    private final PaymentsRepository paymentsRepository;
    private final PaymentsUserService paymentsUserService;
    private final PaymentsAccountService paymentsAccountService;

    // 전체 결제내역조회
    public PaymentsHistoryResponse getPaymentHistory(Long travelAccountId, LocalDate startdate, LocalDate enddate,
                                                     int page) {
        if (travelAccountId == null) {
            return new PaymentsHistoryResponse();
        }

        // 통장 멤버인지 확인
        paymentsAccountService.validateTravelAccountMembers(travelAccountId);
        log.info("통장멤버@@@@@@@@@@@@@@@@@@@@@@@@@@");

        // startdate가 null이면 과거 무한대값으로 설정
        if (startdate == null) {
            startdate = LocalDate.of(1970, 1, 1);
        }

        // enddate가 null이면 오늘 날짜로 설정
        if (enddate == null) {
            enddate = LocalDate.now();
        }

        // LocalDate를 LocalDateTime으로 변환
        LocalDateTime startDateTime = startdate.atStartOfDay(); // 00:00:00
        LocalDateTime endDateTime = enddate.atTime(23, 59, 59); // 23:59:59

        Pageable pageable = PageRequest.of(page, 20);  // 20개씩 페이지 처리
        Page<Payments> paymentsPage = paymentsRepository.findAllByAccountIdDateRange(
                travelAccountId, startDateTime, endDateTime, pageable);

        // 페이지 정보 가져오기
        List<PaymentsHistoryResponse.Data> payments = paymentsPage.getContent().stream()
                .map(this::toPaymentsHistoryData)
                .collect(Collectors.toList());

        // 응답 생성
        return PaymentsHistoryResponse.builder()
                .paymentAccountId(travelAccountId)
                .payments(payments)
                .pagination(
                        PaymentsHistoryResponse.Pagination.builder()
                                .currentPage((long) paymentsPage.getNumber() + 1)
                                .totalPages((long) paymentsPage.getTotalPages())
                                .totalResults(paymentsPage.getTotalElements())
                                .build()
                )
                .build();
    }

    // DTO로 변환
    private PaymentsHistoryResponse.Data toPaymentsHistoryData(Payments payment) {

        return PaymentsHistoryResponse.Data.builder()
                .payId(payment.getPayId())
                .currency(payment.getMerchant().getExchangeCurrency())
                .merchantName(payment.getMerchant().getName())
                .paymentDate(payment.getPaymentDate().toString())
                .krwAmount(payment.getKrwAmount())
                .foreignAmount(payment.getForeignAmount())
                .userName(paymentsUserService.getUserName(payment.getUserId()))
                .category(payment.getMerchant().getCategory()) // 카테고리 필드
                .build();
    }

    public ChartResponse getChart(Long travelAccountId, LocalDate startdate, LocalDate enddate) {

        if (travelAccountId == null) {
            return new ChartResponse(null, null);
        }

        // 통장 멤버인지 확인
        paymentsAccountService.validateTravelAccountMembers(travelAccountId);

        // startdate가 null이면 과거 무한대값으로 설정
        if (startdate == null) {
            startdate = LocalDate.of(1970, 1, 1);
        }

        // enddate가 null이면 오늘 날짜로 설정
        if (enddate == null) {
            enddate = LocalDate.now();
        }

        // LocalDate를 LocalDateTime으로 변환
        LocalDateTime startDateTime = startdate.atStartOfDay(); // 00:00:00
        LocalDateTime endDateTime = enddate.atTime(23, 59, 59); // 23:59:59

        // 카테고리별 총 결제 금액 조회
        List<CategorySummary> categorySummaries = paymentsRepository.findCategorySummaryByAccountIdAndDateRange(
                travelAccountId, startDateTime, endDateTime);

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
        return ChartResponse.builder().totalAmount(totalAmount).category(categoryList).build();
    }

    private double calculatePercent(Long categoryAmount, Long totalAmount) {
        return totalAmount > 0 ? Math.round((categoryAmount * 100.0 / totalAmount) * 100) / 100.0 : 0;
    }

    public PaymentsHistoryCategoryResponse getPaymentsByCategoryName(Long accountId, String categoryName,
                                                                     LocalDate startDate, LocalDate endDate, int page) {
        if (accountId == null) {
            return new PaymentsHistoryCategoryResponse();
        }

        // 통장 멤버인지 확인
        paymentsAccountService.validateTravelAccountMembers(accountId);

        // startDate 가 null 이면 과거 무한대값으로 설정
        if (startDate == null) {
            startDate = LocalDate.of(1970, 1, 1);
        }

        // endDate 가 null 이면 오늘 날짜로 설정
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // LocalDate를 LocalDateTime으로 변환
        LocalDateTime startDateTime = startDate.atStartOfDay(); // 00:00:00
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59); // 23:59:59

        MerchantCategory categoryEnum;
        try {
            categoryEnum = MerchantCategory.valueOf(categoryName);
        } catch (IllegalArgumentException e) {
            throw new PaymentsException("올바르지 않은 카테고리 이름입니다. 전송된 카테고리 이름: " + categoryName, HttpStatus.BAD_REQUEST);
        }

        // 페이지 처리
        Pageable pageable = PageRequest.of(page, 20);  // 한 페이지에 20개의 결과 반환
        Page<com.trabean.payment.entity.Payments> paymentsPage = paymentsRepository.findAllByCategoryAndDateRange(
                accountId, startDateTime, endDateTime,
                categoryEnum, pageable);

        // 전체 결제 금액 계산
        Long categoryTotalAmount = paymentsPage.getContent().stream()
                .mapToLong(com.trabean.payment.entity.Payments::getKrwAmount)
                .sum();

        // 결제 내역 리스트로 변환
        List<PaymentsHistoryCategoryResponse.Payments> payments = paymentsPage.getContent().stream()
                .map(payment -> PaymentsHistoryCategoryResponse.Payments.builder()
                        .payId(payment.getPayId())
                        .currency(payment.getMerchant().getExchangeCurrency())
                        .merchantName(payment.getMerchant().getName())
                        .paymentDate(payment.getPaymentDate().toString())
                        .krwAmount(payment.getKrwAmount())
                        .foreignAmount(payment.getForeignAmount())
                        .userId(payment.getUserId())
                        .build())
                .collect(Collectors.toList());

        // 페이지 정보 생성
        PaymentsHistoryCategoryResponse.Pagination pagination = new PaymentsHistoryCategoryResponse.Pagination(
                (long) paymentsPage.getNumber() + 1,  // 1부터 시작
                (long) paymentsPage.getTotalPages(),
                paymentsPage.getTotalElements()
        );

        // 응답 DTO 생성 및 반환
        return PaymentsHistoryCategoryResponse.builder()
                .categoryName(MerchantCategory.valueOf(categoryName))
                .categoryTotalAmount(categoryTotalAmount)
                .payments(payments)
                .pagination(List.of(pagination))
                .build();
    }
}