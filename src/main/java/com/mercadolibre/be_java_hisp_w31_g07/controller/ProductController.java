package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.UserPostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.FollowersPostsResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerPromoPostsCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.service.IPostService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IProductService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final IProductService productService;
    private final IPostService postService;

    @GetMapping("/promo-post/list")
    public ResponseEntity<UserPostResponseDto> getUserPromoPosts(@RequestParam UUID userId) {
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
            @RequestParam UUID sellerId) {

        SellerPromoPostsCountResponseDto promoPostsCount = postService.getPromoPostsCount(sellerId);
        return new ResponseEntity<>(promoPostsCount, HttpStatus.OK);
    }
    // ------------------------------ START TESTING ------------------------------

    // FOR TESTING PURPOSES ONLY
    // This endpoint is not part of the original requirements
    // and is only used to verify the functionality of the followSeller method.
    // It should be removed in the final version of the code.

    @GetMapping("/post/{postId}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable UUID postId) {
        return new ResponseEntity<>(postService.findPost(postId), HttpStatus.FOUND);
    }

    // ------------------------------ END TESTING ------------------------------

    @GetMapping("/followed/{userId}/list")
    public ResponseEntity<FollowersPostsResponseDto> getLatestPostsFromSellers(@PathVariable UUID userId) {
        return new ResponseEntity<>(postService.getLatestPostsFromSellers(userId), HttpStatus.OK);
    }
}
