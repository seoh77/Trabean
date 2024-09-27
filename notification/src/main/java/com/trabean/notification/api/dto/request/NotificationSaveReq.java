package com.trabean.notification.api.dto.request;


import com.trabean.notification.db.entity.Notification;
import com.trabean.notification.db.entity.NotificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationSaveReq {

    private Long senderId;
    private Long receiverId;
    private Long accountId;
    private NotificationType notificationType;
    private Long amount;


    public Notification toEntity() {

        Notification notification = new Notification();
        notification.setAccountId(accountId);
        notification.setAmount(amount);
        notification.setNotificationType(notificationType);
        notification.setReceiverId(receiverId);
        notification.setSenderId(senderId);
        return notification;
    }
}
