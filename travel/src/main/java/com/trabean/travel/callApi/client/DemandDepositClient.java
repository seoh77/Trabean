package com.trabean.travel.callApi.client;

import com.trabean.travel.callApi.dto.request.GetKrwAccountBalanceRequestDto;
import com.trabean.travel.callApi.dto.response.GetKrwAccountBalanceResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "demandDepositClient", url = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/demandDeposit", configuration = FeignClientsConfiguration.class)
public interface DemandDepositClient {

    /**
     * 계좌번호로 계좌 잔액조회
     */
    @PostMapping("/inquireDemandDepositAccountBalance")
    GetKrwAccountBalanceResponseDto getKrwAccountBalance(
            @RequestBody GetKrwAccountBalanceRequestDto getKrwAccountBalanceRequestDto);

}
