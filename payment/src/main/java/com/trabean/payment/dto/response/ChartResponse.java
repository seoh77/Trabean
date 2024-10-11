package com.trabean.payment.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChartResponse {
    private Long totalAmount;
    private List<Category> category;

    @Getter
    @AllArgsConstructor
    public static class Category {
        private String categoryName;
        private Long amount;
        private Double percent;
    }
}
