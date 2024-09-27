package com.trabean.travel.callApi.client;

import com.trabean.travel.callApi.dto.request.AccountNumberApiRequestDto;
import com.trabean.travel.callApi.dto.request.AdminUserKeyApiRequestDto;
import com.trabean.travel.callApi.dto.request.MemberJoinApiRequestDto;
import com.trabean.travel.callApi.dto.response.AccountNumberApiResponseDto;
import com.trabean.travel.callApi.dto.response.AdminUserKeyApiResponseDto;
import com.trabean.travel.callApi.dto.response.MemberJoinApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account", path = "/api/accounts", configuration = FeignClientsConfiguration.class)
public interface AccountClient {

    /**
     * Account API : accountId로 계좌번호 조회
     */
    @PostMapping("/internal/get-accountNo")
    AccountNumberApiResponseDto getAccount(@RequestBody AccountNumberApiRequestDto accountNumberApiRequestDto);

    /**
     * Account API : 통장 주인의 userKey 조회
     */
    @PostMapping("/internal/get-admin-userKey")
    AdminUserKeyApiResponseDto getAdminUserKey(@RequestBody AdminUserKeyApiRequestDto adminUserKeyApiRequestDto);

    /**
     * Account API : 여행통장 가입
     */
    @PostMapping("/internal/join-member")
    MemberJoinApiResponseDto joinMember(@RequestBody MemberJoinApiRequestDto memberJoinApiRequestDto);

}