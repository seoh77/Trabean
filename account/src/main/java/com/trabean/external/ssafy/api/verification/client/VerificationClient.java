package com.trabean.external.ssafy.api.verification.client;

import com.trabean.config.FeignClientConfig;
import com.trabean.external.ssafy.api.verification.dto.request.CheckAuthCodeRequestDTO;
import com.trabean.external.ssafy.api.verification.dto.request.OpenAccountAuthRequestDTO;
import com.trabean.external.ssafy.api.verification.dto.response.CheckAuthCodeResponseDTO;
import com.trabean.external.ssafy.api.verification.dto.response.OpenAccountAuthResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * SSAFY 금융 API p.202 ~ p.207 1원 인증 관련 요청 처리 클라이언트
 */
@FeignClient(name = "verificationClient", url = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/accountAuth", configuration = FeignClientConfig.class)
public interface VerificationClient {

    /**
     * SSAFY 금융 API p.202 - 1원 송금
     */
    @PostMapping("/openAccountAuth")
    OpenAccountAuthResponseDTO openAccountAuth(@RequestBody OpenAccountAuthRequestDTO requestDTO);

    /**
     * SSAFY 금융 API p.205 - 1원 송금 검증
     */
    @PostMapping("/checkAuthCode")
    CheckAuthCodeResponseDTO checkAuthCode(@RequestBody CheckAuthCodeRequestDTO requestDTO);
}
