package com.mercadolibre.be_java_hisp_w31_g07.controller.implementations;

import com.mercadolibre.be_java_hisp_w31_g07.controller.ISellerController;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.SellerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerAveragePrice;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerFollowersCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.service.ISellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SellerController implements ISellerController {

    private final ISellerService sellerService;

    @Override
    public ResponseEntity<SellerDto> getFollowers(UUID userId) {
        return new ResponseEntity<>(sellerService.findFollowers(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> followSeller(UUID userId, UUID userIdToFollow) {
        sellerService.followSeller(userIdToFollow, userId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> unfollowSeller(UUID userId, UUID userIdToUnfollow) {
        sellerService.unfollowSeller(userIdToUnfollow, userId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<SellerFollowersCountResponseDto> getFollowersCount(UUID userId) {
        return ResponseEntity.ok(sellerService.findFollowersCount(userId));
    }

    @Override
    public ResponseEntity<SellerDto> getSortedFollowers(UUID sellerId, String order) {
        return ResponseEntity.ok(sellerService.sortFollowersByName(sellerId, order));
    }

    @Override
    public ResponseEntity<SellerAveragePrice> getAveragePrice(UUID userId) {
        return new ResponseEntity<>(sellerService.findPricePerPosts(userId), HttpStatus.OK);
    }
}
