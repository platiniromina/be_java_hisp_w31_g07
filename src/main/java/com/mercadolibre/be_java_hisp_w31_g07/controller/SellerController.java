package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.SellerDto;

import java.util.UUID;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.ErrorResponseDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @Operation(summary = "Get followers - [REQ - 3]", description = "Returns a list of the buyers that follow the given seller.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request: seller not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/users/{userId}/followers/list")
    public ResponseEntity<SellerDto> getFollowers(
            @Parameter(description = "Seller id", required = true) @PathVariable UUID userId) {
        return new ResponseEntity<>(sellerService.findFollowers(userId), HttpStatus.OK);
    }

    @Operation(summary = "Follow a seller - [REQ - 1]", description = "Allows a buyer to follow another user who is registered as a seller. This operation updates the buyer's followed sellers list and the seller's followers list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully followed the seller. Response body is empty."),
            @ApiResponse(responseCode = "400", description = "Bad Request: IDs are the same, buyer or seller not found, or already following.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/users/{userId}/follow/{userIdToFollow}")
    public ResponseEntity<Void> followSeller(
            @Parameter(description = "Buyer id", required = true) @PathVariable UUID userId,
            @Parameter(description = "Seller id", required = true) @PathVariable UUID userIdToFollow) {
        sellerService.followSeller(userIdToFollow, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Unfollow a seller - [REQ - 7]", description = "Allows a buyer to unfollow a seller. This operation updates the buyer's followed sellers list and the seller's followers list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully unfollowed the seller. Response body is empty."),
            @ApiResponse(responseCode = "400", description = "Bad Request: buyer or seller not found or buyer not following the given seller.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/users/{userId}/unfollow/{userIdToUnfollow}")
    public ResponseEntity<Void> unfollowSeller(
            @Parameter(description = "Buyer id", required = true) @PathVariable UUID userId,
            @Parameter(description = "Seller id", required = true) @PathVariable UUID userIdToUnfollow) {
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

    @Operation(summary = "Get followers count - [REQ - 2]", description = "Returns the number of buyers that follow the given seller.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request: seller not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/users/{userId}/followers/count")
    public ResponseEntity<SellerFollowersCountResponseDto> getFollowersCount(
            @Parameter(description = "Seller id", required = true) @PathVariable UUID userId) {
        return ResponseEntity.ok(sellerService.findFollowersCount(userId));
    }
}
