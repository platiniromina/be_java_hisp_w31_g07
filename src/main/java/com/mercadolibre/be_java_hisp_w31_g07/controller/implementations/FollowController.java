package com.mercadolibre.be_java_hisp_w31_g07.controller.implementations;

import com.mercadolibre.be_java_hisp_w31_g07.controller.IFollowController;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.SellerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerFollowersCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.service.IFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FollowController implements IFollowController {

    private final IFollowService followService;

    @Override
    public ResponseEntity<SellerDto> getFollowers(UUID userId) {
        return new ResponseEntity<>(followService.findFollowers(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> followSeller(UUID userId, UUID userIdToFollow) {
        followService.follow(userIdToFollow, userId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> unfollowSeller(UUID userId, UUID userIdToUnfollow) {
        followService.unfollow(userIdToUnfollow, userId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<SellerFollowersCountResponseDto> getFollowersCount(UUID userId) {
        return ResponseEntity.ok(followService.findFollowersCount(userId));
    }

    @Override
    public ResponseEntity<SellerDto> getSortedFollowers(UUID sellerId, String order) {
        return ResponseEntity.ok(followService.findSortedFollowersByName(sellerId, order));
    }

    @Override
    public ResponseEntity<BuyerDto> getFollowed(UUID userId) {
        return new ResponseEntity<>(followService.findFollowed(userId), HttpStatus.OK);
    }
}
