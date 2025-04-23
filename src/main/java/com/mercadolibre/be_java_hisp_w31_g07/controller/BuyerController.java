package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.BuyerPurchasesResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.service.IBuyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.service.IBuyerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BuyerController {
    private final IBuyerService buyerService;

    @GetMapping("/users/{userId}/followed/list")
    public ResponseEntity<BuyerDto> getFollowed(@PathVariable UUID userId) {
        return new ResponseEntity<>(buyerService.findFollowed(userId), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/buyer-purchases")
    public ResponseEntity<BuyerPurchasesResponseDto> getBuyerPurchases(@PathVariable UUID userId,
            @RequestParam String product) {
        return new ResponseEntity<>(buyerService.findBuyerPurchase(userId, product), HttpStatus.OK);
    }

}
