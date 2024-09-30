package com.trabean.payment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MerchantCategory {
    TRANSPORTATION, FOOD, SHOPPING, ACCOMMODATION, ACTIVITY, OTHER;

    @JsonCreator
    public static MerchantCategory from(String value) {
        return MerchantCategory.valueOf(value.trim().toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}