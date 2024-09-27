package com.trabean.external.msa.travel.client;

import com.trabean.config.feign.FeignClientConfiguration;
import com.trabean.external.msa.travel.dto.requestDTO.SaveDomesticTravelAccountRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "travel", configuration = FeignClientConfiguration.class)
public interface TravelClient {

    /**
     * 한화 여행통장 생성 요청
     *
     * @param requestDTO
     */
    @PostMapping("/api/travel/krw-account/save")
    void saveDomesticTravelAccount(@RequestBody SaveDomesticTravelAccountRequestDTO requestDTO);
}
