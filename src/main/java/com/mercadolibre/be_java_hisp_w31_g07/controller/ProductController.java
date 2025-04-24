package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.FollowersPostsResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerPromoPostsCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.UserPostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.service.IPostService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final IProductService productService;
    private final IPostService postService;

    @GetMapping("/promo-post/list")
    public ResponseEntity<UserPostResponseDto> getUserPromoPosts(@RequestParam(name = "user_id") UUID userId) {
        return new ResponseEntity<>(productService.getSellerPromoPosts(userId), HttpStatus.OK);
    }

    @PostMapping("/post")
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostDto newPost) {
        PostResponseDto post = postService.createPost(newPost);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PostMapping("/promo-post")
    public ResponseEntity<PostResponseDto> createPromoPost(@RequestBody PostDto newPromoPost) {
        PostResponseDto promoPost = postService.createPost(newPromoPost);
        return new ResponseEntity<>(promoPost, HttpStatus.OK);
    }

    @GetMapping("/promo-post/count")
    public ResponseEntity<SellerPromoPostsCountResponseDto> getUserPromoPostCount(
            @RequestParam(name = "user_id") UUID userId) {
        SellerPromoPostsCountResponseDto promoPostsCount = postService.getPromoPostsCount(userId);
        return new ResponseEntity<>(promoPostsCount, HttpStatus.OK);
    }

    @GetMapping("/followed/{userId}/list")
    public ResponseEntity<FollowersPostsResponseDto> getLatestPostsFromSellers(@PathVariable UUID userId) {
        return new ResponseEntity<>(postService.getLatestPostsFromSellers(userId), HttpStatus.OK);
    }

    @GetMapping("/followed/{userId}/sorted")
    public ResponseEntity<FollowersPostsResponseDto> getSortedPostsByDate(
            @PathVariable UUID userId,
            @RequestParam(name = "order", required = false, defaultValue = "date_desc") String order) {
        FollowersPostsResponseDto response = postService.sortPostsByDate(userId, order);
        return ResponseEntity.ok(response);
    }

}
