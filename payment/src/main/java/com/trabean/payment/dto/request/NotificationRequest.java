package com.trabean.payment.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class NotificationRequest {
    private Long senderId;
    private List<Long> receiverId;
    private Long accountId;
    private String notificationType;
    private Long amount;
}
