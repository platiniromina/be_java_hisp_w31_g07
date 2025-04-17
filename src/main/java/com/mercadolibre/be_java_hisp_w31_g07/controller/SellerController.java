package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.service.ISellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SellerController {
    private final ISellerService sellerService;

    @GetMapping("/users/{userId}/followers/list")
        public ResponseEntity<?> getFollowers(@PathVariable UUID userId){
            return new ResponseEntity<>(sellerService.findFollowers(userId), HttpStatus.OK);
        }

}
