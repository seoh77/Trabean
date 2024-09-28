package com.trabean.external.msa.travel.client;

import com.trabean.config.feign.FeignClientConfig;
import com.trabean.external.msa.travel.dto.requestDTO.SaveDomesticTravelAccountRequestDTO;
import com.trabean.external.msa.travel.dto.responseDTO.DomesticTravelAccountInfoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "travel", configuration = FeignClientConfig.class)
public interface TravelClient {

    /**
     * 한화 여행통장 생성 요청
     */
    @PostMapping("/api/travel/krw-account/save")
    void saveDomesticTravelAccount(@RequestBody SaveDomesticTravelAccountRequestDTO requestDTO);

    /**
     * 한화 여행통장 정보 요청
     */
    @GetMapping("/api/travel/info/{accountId}")
    DomesticTravelAccountInfoResponseDTO getDomesticTravelAccountInfo(@PathVariable Long accountId);
}
