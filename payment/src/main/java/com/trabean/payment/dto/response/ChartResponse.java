package com.trabean.payment.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ChartResponse {
    private Long totalAmount;
    private List<Category> category;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Category {
        private String categoryName;
        private Long amount;
        private Double percent;
    }
}
