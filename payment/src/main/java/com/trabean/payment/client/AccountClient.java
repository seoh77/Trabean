package com.trabean.payment.client;

import com.trabean.payment.dto.response.AccountNoResponse;
import com.trabean.payment.dto.response.UserRoleResponse;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "account", path = "/api/accounts", configuration = FeignClientConfiguration.class)
public interface AccountClient {

    @PostMapping(value = "/internal/get-userRole", consumes = MediaType.APPLICATION_JSON_VALUE)
    UserRoleResponse getUserRole(@RequestBody String requestBody);

    @PostMapping(value = "/internal/get-accountNo", consumes = MediaType.APPLICATION_JSON_VALUE)
    AccountNoResponse getAccountNumber(@RequestBody String requestBody);

    @PostMapping(value = "internal/verify-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, String> validateAccountPassword(@RequestBody String requestBody);

    @PostMapping(value = "internal/get-admin-userKey", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, String> getAdminUser(@RequestBody String requestBody);
}
