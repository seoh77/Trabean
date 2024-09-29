package com.trabean.external.msa.notification.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationRequestDTO {

    @JsonProperty("senderId")
    private Long senderId;

    @JsonProperty("receiverId")
    private Long receiverId;

    @JsonProperty("accountId")
    private Long accountId;

    @JsonProperty("notificationType")
    private String notificationType;

    @JsonProperty("amount")
    private Long amount;
}
