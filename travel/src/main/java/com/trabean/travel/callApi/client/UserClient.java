package com.trabean.travel.callApi.client;

import com.trabean.travel.callApi.dto.response.UserEmailApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user", configuration = FeignClientsConfiguration.class)
public interface UserClient {

    /**
     * User API : userID로 user email 반환
     */
    @GetMapping("/api/user/useremail/{userId}")
    UserEmailApiResponseDto getUserEmail(@PathVariable Long userId);

}
