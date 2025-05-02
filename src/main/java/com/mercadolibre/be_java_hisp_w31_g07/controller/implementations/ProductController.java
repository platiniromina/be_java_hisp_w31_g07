package com.mercadolibre.be_java_hisp_w31_g07.controller.implementations;

import com.mercadolibre.be_java_hisp_w31_g07.controller.IProductController;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.*;
import com.mercadolibre.be_java_hisp_w31_g07.service.IPostOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProductController implements IProductController {

    private final IPostOrchestrator postOrchestrator;

    @Override
    public ResponseEntity<UserPostResponseDto> getUserPromoPosts(UUID userId) {
        return new ResponseEntity<>(postOrchestrator.findUserPromoPosts(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PostResponseDto> createPost(PostDto newPost) {
        return new ResponseEntity<>(postOrchestrator.createPost(newPost), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PostResponseDto> createPromoPost(PostDto newPromoPost) {
        return new ResponseEntity<>(postOrchestrator.createPost(newPromoPost), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<SellerPromoPostsCountResponseDto> getUserPromoPostCount(UUID userId) {
        return new ResponseEntity<>(postOrchestrator.findPromoPostsCount(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<FollowersPostsResponseDto> getLatestPostsFromSellers(UUID userId) {
        return new ResponseEntity<>(postOrchestrator.findLatestPostsFromSellers(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<FollowersPostsResponseDto> getSortedPostsByDate(UUID userId, String order) {
        return ResponseEntity.ok(postOrchestrator.findLatestPostsFromSellersSortedByDate(userId, order));
    }

    @Override
    public ResponseEntity<PostResponseDto> getPost(UUID postId) {
        return new ResponseEntity<>(postOrchestrator.findPost(postId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<SellerAveragePrice> getAveragePricePerPostsBySellerId(UUID userId) {
        return new ResponseEntity<>(postOrchestrator.findPricePerPostsBySellerId(userId), HttpStatus.OK);
    }
}
