package com.mercadolibre.be_java_hisp_w31_g07.controller;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.service.IBuyerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Buyer", description = "Operations related to buyers")
public class BuyerController {
    private final IBuyerService buyerService;

    @Operation(summary = "Get followings - [REQ - 4]", description = "Returns a list of the sellers that the given buyer is following.")
    @GetMapping("/users/{userId}/followed/list")
    public ResponseEntity<BuyerDto> getFollowed(@PathVariable UUID userId) {
        return new ResponseEntity<>(buyerService.findFollowed(userId), HttpStatus.OK);
    }

    // FOR TESTING PURPOSES ONLY
    // This endpoint is not part of the original requirements
    // and is only used to verify the functionality of the followSeller method.
    // It should be removed in the final version of the code.
    @GetMapping("/users/test/{userId}/buyer")
    public ResponseEntity<Buyer> getBuyer(@PathVariable UUID userId) {
        Buyer buyer = buyerService.findBuyerById(userId);
        return ResponseEntity.ok(buyer);
    }
}
