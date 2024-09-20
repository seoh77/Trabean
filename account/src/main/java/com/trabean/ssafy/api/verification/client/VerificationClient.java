package com.trabean.ssafy.api.verification.client;

import com.trabean.ssafy.api.verification.dto.requestDTO.CheckAuthCodeRequestDTO;
import com.trabean.ssafy.api.verification.dto.requestDTO.OpenAccountAuthRequestDTO;
import com.trabean.ssafy.api.verification.dto.responseDTO.CheckAuthCodeResponseDTO;
import com.trabean.ssafy.api.verification.dto.responseDTO.OpenAccountAuthResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "verificationClient", url = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/accountAuth")
public interface VerificationClient {

    // SSAFY 금융 API p.202 - 1원 송금
    @PostMapping("/openAccountAuth")
    OpenAccountAuthResponseDTO openAccountAuth(@RequestBody OpenAccountAuthRequestDTO requestDTO);

    // SSAFY 금융 API p.205 - 1원 송금 검증
    @PostMapping("/checkAuthCode")
    CheckAuthCodeResponseDTO checkAuthCode(@RequestBody CheckAuthCodeRequestDTO requestDTO);
}
