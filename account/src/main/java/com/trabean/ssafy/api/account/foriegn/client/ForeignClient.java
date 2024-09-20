package com.trabean.ssafy.api.account.foriegn.client;

import com.trabean.ssafy.api.account.foriegn.dto.requestDTO.CreateForeignCurrencyDemandDepositAccountRequestDTO;
import com.trabean.ssafy.api.account.foriegn.dto.requestDTO.InquireForeignCurrencyDemandDepositAccountListRequestDTO;
import com.trabean.ssafy.api.account.foriegn.dto.requestDTO.InquireForeignCurrencyDemandDepositAccountRequestDTO;
import com.trabean.ssafy.api.account.foriegn.dto.responseDTO.CreateForeignCurrencyDemandDepositAccountResponseDTO;
import com.trabean.ssafy.api.account.foriegn.dto.responseDTO.InquireForeignCurrencyDemandDepositAccountListResponseDTO;
import com.trabean.ssafy.api.account.foriegn.dto.responseDTO.InquireForeignCurrencyDemandDepositAccountResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "foreignClient", url = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/demandDeposit/foreignCurrency")
public interface ForeignClient {

    // SSAFY 금융 API p.228 - 외화 계좌 생성
    @PostMapping("/createForeignCurrencyDemandDepositAccount")
    CreateForeignCurrencyDemandDepositAccountResponseDTO createForeignCurrencyDemandDepositAccount(@RequestBody CreateForeignCurrencyDemandDepositAccountRequestDTO requestDTO);

    // SSAFY 금융 API p.231 - 계좌 목록 조회
    @PostMapping("/inquireForeignCurrencyDemandDepositAccountList")
    InquireForeignCurrencyDemandDepositAccountListResponseDTO inquireForeignCurrencyDemandDepositAccountList(@RequestBody InquireForeignCurrencyDemandDepositAccountListRequestDTO requestDTO);

    // SSAFY 금융 API p.235 - 외화 계좌 조회 (단건)
    @PostMapping("/inquireForeignCurrencyDemandDepositAccount")
    InquireForeignCurrencyDemandDepositAccountResponseDTO inquireForeignCurrencyDemandDepositAccount(@RequestBody InquireForeignCurrencyDemandDepositAccountRequestDTO requestDTO);

    // SSAFY 금융 API p.238 - 외화 예금주 조회
    // 만드나?

    // SSAFY 금융 API p.240 - 외화 계좌 잔액 조회
    // 만드나?

    // SSAFY 금융 API p.243 - 외화 계좌 출금
    // 만드나?

    // SSAFY 금융 API p.246 - 외화 계좌 입금
    // 만드나?

    // SSAFY 금융 API p.249 - 외화 계좌 이체
    // 만드나?

    // SSAFY 금융 API p.252 - 외화 계좌 이체 한도 변경
    // 만드나?

    // SSAFY 금융 API p.256 - 외화 계좌 거래 내역 조회
    // 만들자

    // SSAFY 금융 API p.260 - 계좌 거래 내역 조회 (단건)
    // 만들자

    // SSAFY 금융 API p.263 - 외화 계좌 해지
    // 만들자
}
