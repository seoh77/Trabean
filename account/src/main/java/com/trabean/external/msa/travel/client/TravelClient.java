package com.trabean.external.msa.travel.client;

import com.trabean.config.feign.FeignClientConfiguration;
import com.trabean.external.msa.travel.dto.requestDTO.SaveDomesticTravelAccountRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "travel", configuration = FeignClientConfiguration.class)
public interface TravelClient {

    @PostMapping("/api/travel/krw-account/save")
    ResponseEntity<Void> saveDomesticTravelAccount(@RequestBody SaveDomesticTravelAccountRequestDTO requestDTO);
}
