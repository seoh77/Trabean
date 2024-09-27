
package com.trabean.external.msa.travel.client;

import java.util.Map;

import com.trabean.config.feign.FeignClientConfiguration;
import com.trabean.external.msa.travel.dto.requestDTO.FindUserKeyByUserIdRequestDTO;
import com.trabean.external.msa.travel.dto.responseDTO.FindUserKeyByUserIdResponseDTO;
import com.trabean.internal.dto.requestDTO.AdminUserKeyRequestDTO;
import com.trabean.internal.dto.responseDTO.AdminUserKeyResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user", configuration = FeignClientConfiguration.class)
public interface UserClient {

    @GetMapping(value = "/api/user/name/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, String> getUserName(@PathVariable Long userId);

    @PostMapping(value = "/api/user/getuserkey", consumes = MediaType.APPLICATION_JSON_VALUE)
    FindUserKeyByUserIdResponseDTO getUserKey(@RequestBody FindUserKeyByUserIdRequestDTO requestDTO);
}