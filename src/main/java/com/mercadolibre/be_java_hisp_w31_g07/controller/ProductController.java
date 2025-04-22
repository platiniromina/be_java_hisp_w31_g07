package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.ErrorResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.FollowersPostsResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.service.IPostService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@Tag(name = "Product", description = "Operations related to products and posts")
public class ProductController {
    private final IProductService productService;
    private final IPostService postService;

    @Operation(summary = "Create a new post - [REQ - 5]", description = "Creates a new post with a product associated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request: seller not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/post")
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostDto newPost) {
        PostResponseDto post = postService.createPost(newPost);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @Operation(summary = "Create a new post with discount - [REQ - 10]", description = "Creates a new post with a product associated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request: buyer not found or the buyer is not following any sellers.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/promo-post")
    public ResponseEntity<PostResponseDto> createPromoPost(@RequestBody PostDto newPromoPost) {
        PostResponseDto promoPost = postService.createPost(newPromoPost);
        return new ResponseEntity<>(promoPost, HttpStatus.OK);
    }

    @Operation(summary = "Get latest posts from sellers - [REQ - 6]", description = "Returns the most recent posts from sellers followed by the given buyer. Only includes posts from the last two weeks, sorted by newest first.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request: buyer not found or the buyer is not following any sellers.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/followed/{userId}/list")
    public ResponseEntity<FollowersPostsResponseDto> getLatestPostsFromSellers(
            @Parameter(description = "Buyer id", required = true) @PathVariable UUID userId) {
        return new ResponseEntity<>(postService.getLatestPostsFromSellers(userId), HttpStatus.OK);
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


