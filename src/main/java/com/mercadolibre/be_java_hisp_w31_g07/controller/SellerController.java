package com.mercadolibre.be_java_hisp_w31_g07.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerFollowersCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.service.ISellerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SellerController {
    private final ISellerService sellerService;

    @GetMapping("/users/{userId}/followers/count")
    public ResponseEntity<SellerFollowersCountResponseDto> getFollowersCount(@PathVariable UUID userId) {
        return ResponseEntity.ok(sellerService.findFollowersCount(userId));
    }
}
