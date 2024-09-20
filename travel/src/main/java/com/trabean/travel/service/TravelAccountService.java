package com.trabean.travel.service;

import com.trabean.travel.dto.response.TravelAccountIdResponseDto;
import com.trabean.travel.dto.response.TravelAccountResponseDto;
import com.trabean.travel.dto.response.TravelListAccountResponseDto;
import com.trabean.travel.entity.ForeignTravelAccount;
import com.trabean.travel.entity.KrwTravelAccount;
import com.trabean.travel.repository.ForeignTravelAccountRepository;
import com.trabean.travel.repository.KrwTravelAccountRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TravelAccountService {

    private final ForeignTravelAccountRepository foreignTravelAccountRepository;
    private final KrwTravelAccountRepository krwTravelAccountRepository;

    @Transactional
    public Long updateTravelAccountName(Long accountId, String accountName) {
        KrwTravelAccount account = krwTravelAccountRepository.findByAccountId(accountId);
        account.setAccountName(accountName);
        return accountId;
    }

    public TravelListAccountResponseDto findAllTravelAccount(Long parentId) {
        KrwTravelAccount krwTravelAccount = krwTravelAccountRepository.findByAccountId(parentId);

        if (krwTravelAccount == null) {
            throw new RuntimeException("KRW 계좌를 찾을 수 없습니다.");
        }

        List<ForeignTravelAccount> foreignTravelAccounts = foreignTravelAccountRepository.findByParentAccountId(
                parentId);
        List<TravelAccountResponseDto> list = new ArrayList<>();

        // 계좌번호 조회
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://j11a604.p.ssafy.io:8081/api/accounts/get-account-number";
        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("accountId", parentId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

        Map<String, String> responseBody = responseEntity.getBody();
        String accountKRWNo = responseBody.get("accountNo");

        System.out.println("accountKRWNo = " + accountKRWNo);

        // krwTravelAccount 잔액조회
        Double accountKRWBalance = 0.0;

        list.add(new TravelAccountResponseDto(krwTravelAccount.getAccountId(), "한국", "KRW", accountKRWBalance));

        for (ForeignTravelAccount foreignTravelAccount : foreignTravelAccounts) {
            Long accountId = foreignTravelAccount.getAccountId();
            String exchangeCurrency = foreignTravelAccount.getExchangeCurrency();
            String country = "";

            if (exchangeCurrency.equals("USD")) {
                country = "미국";
            } else if (exchangeCurrency.equals("EUR")) {
                country = "유럽";
            } else if (exchangeCurrency.equals("JPY")) {
                country = "일본";
            } else if (exchangeCurrency.equals("GBP")) {
                country = "영국";
            } else if (exchangeCurrency.equals("CHF")) {
                country = "스위스";
            } else if (exchangeCurrency.equals("CAD")) {
                country = "캐나다";
            }

            // 외화통장 잔액조회
            Double accountBalance = 0.0;

            list.add(new TravelAccountResponseDto(accountId, country, exchangeCurrency, accountBalance));
        }

        return new TravelListAccountResponseDto(krwTravelAccount.getAccountName(), list);
    }

    public TravelAccountIdResponseDto findAccountIdByCurrency(Long parentId, String currency) {
        if (currency.equals("KRW")) {
            KrwTravelAccount krwTravelAccount = krwTravelAccountRepository.findByAccountId(parentId);
            return new TravelAccountIdResponseDto(krwTravelAccount.getAccountId());
        } else {
            List<ForeignTravelAccount> foreignTravelAccounts = foreignTravelAccountRepository.findByParentAccountId(
                    parentId);

            for (ForeignTravelAccount foreignTravelAccount : foreignTravelAccounts) {
                String accountCurreny = foreignTravelAccount.getExchangeCurrency();
                if (currency.equals(accountCurreny)) {
                    return new TravelAccountIdResponseDto(foreignTravelAccount.getAccountId());
                }
            }
        }

        return null;
    }

}
