package com.trabean.travel.callApi.client;

import com.trabean.travel.callApi.dto.request.AccountNumberApiRequestDto;
import com.trabean.travel.callApi.dto.response.AccountNumberApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account", configuration = FeignClientsConfiguration.class)
public interface AccountClient {

    /**
     * Account API : accountId로 계좌번호 조회
     */
    @PostMapping("/get-account-number")
    AccountNumberApiResponseDto getAccount(@RequestBody AccountNumberApiRequestDto accountNumberApiRequestDto);

}