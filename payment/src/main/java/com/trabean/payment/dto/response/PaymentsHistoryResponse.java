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
public class PaymentsHistoryResponse {
    private Long paymentAccountId;
    private List<Data> payments;
    private Pagination pagination;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Data {
        private Long payId;
        private String currency;
        private String merchantName;
        private String paymentDate;
        private Long krwAmount;
        private Double foreignAmount;
        private String userName;
        private MerchantCategory category;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Pagination {
        private Long currentPage;
        private Long totalPages;
        private Long totalResults;
    }
}
