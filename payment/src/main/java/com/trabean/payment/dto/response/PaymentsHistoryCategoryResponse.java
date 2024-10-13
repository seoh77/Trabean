package com.trabean.payment.dto.response;

import com.trabean.payment.enums.MerchantCategory;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentsHistoryCategoryResponse {
    private MerchantCategory categoryName;
    private Long categoryTotalAmount;
    private List<Payments> payments;
    private List<Pagination> pagination;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Payments {
        private Long payId;
        private String currency;
        private String merchantName;
        private String paymentDate;
        private Long krwAmount;
        private Double foreignAmount;
        private Long userId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private Long currentPage;
        private Long totalPages;
        private Long totalResults;
    }
}
