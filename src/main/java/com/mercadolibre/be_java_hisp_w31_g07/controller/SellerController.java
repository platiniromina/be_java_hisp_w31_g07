package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.SellerDto;

import java.util.UUID;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerAveragePrice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerFollowersCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.service.ISellerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Seller", description = "Operations related to sellers")
public class SellerController {
    private final ISellerService sellerService;

    @GetMapping("/users/{userId}/followers/list")
    public ResponseEntity<SellerDto> getFollowers(@PathVariable UUID userId) {
        return new ResponseEntity<>(sellerService.findFollowers(userId), HttpStatus.OK);
    }

    @Operation(summary = "Follow a seller", description = "Allows a buyer to follow another user who is registered as a seller. This operation updates the buyer's followed sellers list and the seller's followers list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully followed the seller. Response body is empty."),
            @ApiResponse(responseCode = "400", description = "Bad Request: IDs are the same, buyer or seller not found, or already following.")
    })
    @PostMapping("/users/{userId}/follow/{userIdToFollow}")
    public ResponseEntity<Void> followSeller(
            @Parameter(description = "ID of the buyer who wants to follow the seller", required = true) @PathVariable UUID userId,
            @Parameter(description = "ID of the seller to be followed", required = true) @PathVariable UUID userIdToFollow) {
        sellerService.followSeller(userIdToFollow, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{userId}/unfollow/{userIdToUnfollow}")
    public ResponseEntity<Void> unfollowSeller(
            @PathVariable UUID userId,
            @PathVariable UUID userIdToUnfollow) {
        sellerService.unfollowSeller(userIdToUnfollow, userId);
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

    @GetMapping("/{sellerId}/followers")
    public ResponseEntity<SellerDto> getSortedFollowers(
            @PathVariable UUID sellerId,
            @RequestParam String order) {
        SellerDto sellerDto = sellerService.sortFollowersByName(sellerId, order);
        return ResponseEntity.ok(sellerDto);
    }

    @GetMapping("/users/{userId}/average-post-price")
    public ResponseEntity<SellerAveragePrice> getAveragePrice(@PathVariable UUID userId) {
        return new ResponseEntity<>(sellerService.findPricePerPosts(userId), HttpStatus.OK);
    }
}