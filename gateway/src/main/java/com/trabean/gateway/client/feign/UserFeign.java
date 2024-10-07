package com.trabean.gateway.client.feign;

import com.trabean.gateway.client.dto.request.UserIdReq;
import com.trabean.gateway.client.dto.response.UserKeyRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION", path = "/api/user") // 서비스 이름 설정
public interface UserFeign {

    @PostMapping("/getuserkey")
    UserKeyRes getUserKey(@RequestBody UserIdReq userIdReq);
}