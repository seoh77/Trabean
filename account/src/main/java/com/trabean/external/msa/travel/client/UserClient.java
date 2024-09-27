
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

@FeignClient(name = "userClient", url = "http://j11a604.p.ssafy.io:8086/api/user", configuration = FeignClientConfiguration.class)
public interface UserClient {

    @GetMapping(value = "/name/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, String> getUserName(@PathVariable Long userId);

    @PostMapping("/getuserkey")
    FindUserKeyByUserIdResponseDTO getUserKey(@RequestBody FindUserKeyByUserIdRequestDTO requestDTO);
}