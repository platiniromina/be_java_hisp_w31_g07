package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.SellerDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.service.ISellerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SellerController {
    private final ISellerService sellerService;

    @GetMapping("/users/{userId}/followers/list")
    public ResponseEntity<SellerDto> getFollowers(@PathVariable UUID userId) {
        return new ResponseEntity<>(sellerService.findFollowers(userId), HttpStatus.OK);
    }

    @PostMapping("/users/{userId}/follow/{userIdToFollow}")
    public ResponseEntity<Void> followSeller(
            @PathVariable UUID userId,
            @PathVariable UUID userIdToFollow) {
        sellerService.followSeller(userIdToFollow, userId);
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

}
