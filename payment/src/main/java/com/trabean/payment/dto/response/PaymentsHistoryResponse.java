package com.trabean.payment.dto.response;

import com.trabean.payment.enums.MerchantCategory;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentsHistoryResponse {
    private Long paymentAccountId;
    private List<Data> payments;
    private Pagination pagination;

    @Getter
    @AllArgsConstructor
    public static class Data {
        private Long payId;
        private Long merchantId;
        private String merchantName;
        private String paymentDate;
        private Long krwAmount;
        private Double foreignAmount;
        private String userName;
        private MerchantCategory category;
    }

    @Getter
    @AllArgsConstructor
    public static class Pagination {
        private Long currentPage;
        private Long totalPages;
        private Long totalResults;
    }
}
