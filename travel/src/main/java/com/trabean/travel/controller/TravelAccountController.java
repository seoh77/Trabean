package com.trabean.travel.controller;

import com.trabean.travel.dto.response.TravelListAccountResponseDto;
import com.trabean.travel.service.TravelAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
public class TravelAccountController {

    private final TravelAccountService travelAccountService;

    @GetMapping("{parentAccountId}")
    public ResponseEntity<TravelListAccountResponseDto> getTravelListAccount(@PathVariable Long parentAccountId) {
        return ResponseEntity.ok(travelAccountService.findAllTravelAccount(parentAccountId));
    }
}
