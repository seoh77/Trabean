package com.trabean.payment.client;

import com.trabean.payment.FeignClientConfig;
import com.trabean.payment.dto.response.AccountNoResponse;
import com.trabean.payment.dto.response.TravelAccountMemberListResponse;
import com.trabean.payment.dto.response.UserRoleResponse;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "account", configuration = FeignClientConfig.class)
public interface AccountClient {

    @PostMapping(value = "/api/accounts/internal/get-userRole", consumes = MediaType.APPLICATION_JSON_VALUE)
    UserRoleResponse getUserRole(@RequestBody String requestBody);

    @PostMapping(value = "/api/accounts/internal/get-accountNo", consumes = MediaType.APPLICATION_JSON_VALUE)
    AccountNoResponse getAccountNumber(@RequestBody String requestBody);

    @PostMapping(value = "/api/accounts/internal/verify-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, String> validateAccountPassword(@RequestBody String requestBody);

    @PostMapping(value = "/api/accounts/internal/get-admin-userKey", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, String> getAdminUser(@RequestBody String requestBody);

    @PostMapping(value = "/api/accounts/internal/get-travel-account-members", consumes = MediaType.APPLICATION_JSON_VALUE)
    TravelAccountMemberListResponse getTravelAccountMembers(@RequestBody String requestBody);
}
