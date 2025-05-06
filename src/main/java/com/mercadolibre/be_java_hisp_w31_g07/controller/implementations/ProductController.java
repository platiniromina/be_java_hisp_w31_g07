package com.mercadolibre.be_java_hisp_w31_g07.controller.implementations;

import com.mercadolibre.be_java_hisp_w31_g07.controller.IProductController;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.*;
import com.mercadolibre.be_java_hisp_w31_g07.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProductController implements IProductController {

    private final IPostService postService;

    @Override
    public ResponseEntity<UserPostResponseDto> getUserPromoPosts(UUID userId) {
        return new ResponseEntity<>(postService.getSellerPromoPosts(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PostResponseDto> createPost(PostDto newPost) {
        return new ResponseEntity<>(postService.createPost(newPost), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PostResponseDto> createPromoPost(PostDto newPromoPost) {
        return new ResponseEntity<>(postService.createPost(newPromoPost), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<FollowersPostsResponseDto> getLatestPostsFromSellers(UUID userId) {
        return new ResponseEntity<>(postService.getLatestPostsFromSellers(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<FollowersPostsResponseDto> getSortedPostsByDate(UUID userId, String order) {
        return ResponseEntity.ok(postService.sortPostsByDate(userId, order));
    }

    @Override
    public ResponseEntity<SellerPromoPostsCountResponseDto> getUserPromoPostCount(UUID userId) {
        return new ResponseEntity<>(postService.getPromoPostsCount(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PostResponseDto> getPost(UUID postId) {
        return new ResponseEntity<>(postService.findPost(postId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<SellerAveragePriceDto> getAveragePrice(UUID userId) {
        return new ResponseEntity<>(postService.findPricePerPosts(userId), HttpStatus.OK);
    }
}
