package com.trabean.account.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LastTransactionListResponseDTO {

    @JsonProperty("accountList")
    private List<Info> accountList;

    @Builder
    @Getter
    @EqualsAndHashCode
    public static class Info {
        private Long accountId;
        private String accountNo;
        private String adminName;
    }
}
