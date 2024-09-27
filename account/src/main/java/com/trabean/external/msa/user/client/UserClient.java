package com.trabean.external.msa.user.client;

import com.trabean.config.feign.FeignClientConfiguration;
import com.trabean.external.msa.user.dto.request.UserKeyRequestDTO;
import com.trabean.external.msa.user.dto.response.UserKeyResponseDTO;
import com.trabean.external.msa.user.dto.response.UserNameResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user", configuration = FeignClientConfiguration.class)
public interface UserClient {

    @GetMapping("/api/user/name/{userId}")
    UserNameResponseDTO getUserName(@PathVariable Long userId);

    @PostMapping("/api/user/getuserkey")
    UserKeyResponseDTO getUserKey(@RequestBody UserKeyRequestDTO requestDTO);
}
