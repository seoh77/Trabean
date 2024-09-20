package com.trabean.travel.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trabean.travel.dto.response.TravelAccountIdResponseDto;
import com.trabean.travel.dto.response.TravelListAccountResponseDto;
import com.trabean.travel.entity.KrwTravelAccount;
import com.trabean.travel.service.KrwTravelAccountService;
import com.trabean.travel.service.TargetAmountService;
import com.trabean.travel.service.TravelAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
public class TravelAccountController {

    private final TravelAccountService travelAccountService;
    private final TargetAmountService targetAmountService;
    private final KrwTravelAccountService krwTravelAccountService;

    @GetMapping("{parentAccountId}")
    public ResponseEntity<TravelListAccountResponseDto> getTravelListAccount(@PathVariable Long parentAccountId)
            throws JsonProcessingException {
        return ResponseEntity.ok(travelAccountService.findAllTravelAccount(parentAccountId));
    }

    @PutMapping("{accountId}")
    public ResponseEntity<Void> updateTravelAccountName(@PathVariable Long accountId, @RequestBody String accountName) {
        travelAccountService.updateTravelAccountName(accountId, accountName);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("{accountId}/targetAmount")
    public ResponseEntity<Void> updateTargetAmount(@PathVariable Long accountId, @RequestBody Long targetAmount) {
        targetAmountService.updateTargetAmount(accountId, targetAmount);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<TravelAccountIdResponseDto> getTravelAccountIdByCurrency(@RequestParam Long accountId,
                                                                                   @RequestParam String currency) {
        TravelAccountIdResponseDto travelAccountIdResponseDto = travelAccountService.findAccountIdByCurrency(accountId,
                currency);

        if (travelAccountIdResponseDto != null) {
            return ResponseEntity.ok(travelAccountIdResponseDto);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/krw-account/save")
    public ResponseEntity<Void> saveKrwAccountSave(@RequestBody KrwTravelAccount krwTravelAccount) {
        krwTravelAccountService.save(krwTravelAccount);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
