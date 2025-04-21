package com.mercadolibre.be_java_hisp_w31_g07.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Operation(summary = "Follow a seller", description = "Allows a user to follow another user who is registered as a seller. This operation updates the user's followed sellers list and the seller's followers list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully followed the seller. Response body is empty."),
            @ApiResponse(responseCode = "400", description = "Bad Request: IDs are the same, user or seller not found, or already following.")
    })
    @PostMapping("/users/{userId}/follow/{userIdToFollow}")
    public ResponseEntity<Void> followSeller(
            @Parameter(description = "ID of the user who wants to follow the seller", required = true) @PathVariable UUID userId,
            @Parameter(description = "ID of the seller to be followed", required = true) @PathVariable UUID userIdToFollow) {
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
