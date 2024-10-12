package com.trabean.external.ssafy.api.foriegn.client;

import com.trabean.config.FeignClientConfig;
import com.trabean.external.ssafy.api.foriegn.dto.request.CreateForeignCurrencyDemandDepositAccountRequestDTO;
import com.trabean.external.ssafy.api.foriegn.dto.request.InquireForeignCurrencyDemandDepositAccountRequestDTO;
import com.trabean.external.ssafy.api.foriegn.dto.response.CreateForeignCurrencyDemandDepositAccountResponseDTO;
import com.trabean.external.ssafy.api.foriegn.dto.response.InquireForeignCurrencyDemandDepositAccountResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * SSAFY 금융 API p.222 ~ p.265 외화 수시입출금 관련 요청 처리 클라이언트
 */
@FeignClient(name = "foreignClient", url = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/demandDeposit/foreignCurrency", configuration = FeignClientConfig.class)
public interface ForeignClient {

    /**
     * SSAFY 금융 API p.228 - 외화 계좌 생성
     */
    @PostMapping("/createForeignCurrencyDemandDepositAccount")
    CreateForeignCurrencyDemandDepositAccountResponseDTO createForeignCurrencyDemandDepositAccount(@RequestBody CreateForeignCurrencyDemandDepositAccountRequestDTO requestDTO);

    /**
     * SSAFY 금융 API p.235 - 외화 계좌 조회 (단건)
     */
    @PostMapping("/inquireForeignCurrencyDemandDepositAccount")
    InquireForeignCurrencyDemandDepositAccountResponseDTO inquireForeignCurrencyDemandDepositAccount(@RequestBody InquireForeignCurrencyDemandDepositAccountRequestDTO requestDTO);
}
