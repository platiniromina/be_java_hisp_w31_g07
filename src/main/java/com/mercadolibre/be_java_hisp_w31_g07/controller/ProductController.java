package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.FollowersPostsResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.service.IPostService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final IProductService productService;
    private final IPostService postService;

    @GetMapping("/followed/{userId}/list")
    public ResponseEntity<FollowersPostsResponseDto> getLatestPostsFromSellers(@PathVariable UUID userId) {
        List<PostResponseDto> posts = postService.getLatestPostsFromSellers(userId);
        FollowersPostsResponseDto response = new FollowersPostsResponseDto(userId, posts);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
