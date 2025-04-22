package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.service.IPostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@Tag(name = "Post", description = "Operations related to posts and products")
public class PostController {
    private final IPostService postService;

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
}
