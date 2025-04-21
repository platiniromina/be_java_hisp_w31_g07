package com.mercadolibre.be_java_hisp_w31_g07.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerFollowersCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.service.ISellerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SellerController {
    private final ISellerService sellerService;

    @PostMapping("/users/{userId}/follow/{userIdToFollow}")
    public ResponseEntity<Void> followSeller(
            @PathVariable UUID userId,
            @PathVariable UUID userIdToFollow) {
        sellerService.followSeller(userId, userIdToFollow);
        return ResponseEntity.ok().build();
    }

    // FOR TESTING PURPOSES ONLY
    // This endpoint is not part of the original requirements
    // and is only used to verify the functionality of the followSeller method.
    // It should be removed in the final version of the code.
    @GetMapping("/users/test/{userId}/seller")
    public ResponseEntity<Seller> getSeller(@PathVariable UUID userId) {
        Seller seller = sellerService.findSellerById(userId);
        return ResponseEntity.ok(seller);
    }

    @GetMapping("/users/{userId}/followers/count")
    public ResponseEntity<SellerFollowersCountResponseDto> getFollowersCount(@PathVariable UUID userId) {
        return ResponseEntity.ok(sellerService.findFollowersCount(userId));
    }
}
