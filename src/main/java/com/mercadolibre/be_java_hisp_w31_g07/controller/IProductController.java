package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Product", description = "Operations related to products and posts")
public interface IProductController {

    @GetMapping("/products/promo-post/list")
    ResponseEntity<UserPostResponseDto> getUserPromoPosts(@RequestParam(name = "user_id") UUID userId);

    @Operation(summary = "Create a new post - [REQ - 5]", description = "Creates a new post with a product associated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request: seller not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/products/post")
    ResponseEntity<PostResponseDto> createPost(@Valid @RequestBody PostDto newPost);

    @Operation(summary = "Create a new post with discount - [REQ - 10]", description = "Creates a new post with a product associated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request: buyer not found or the buyer is not following any sellers.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/products/promo-post")
    ResponseEntity<PostResponseDto> createPromoPost(@Valid @RequestBody PostDto newPromoPost);

    @Operation(summary = "Get latest posts from sellers - [REQ - 6]", description = "Returns the most recent posts from sellers followed by the given buyer. Only includes posts from the last two weeks, sorted by newest first.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request: buyer not found or the buyer is not following any sellers.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/products/followed/{userId}/list")
    ResponseEntity<FollowersPostsResponseDto> getLatestPostsFromSellers(
            @Parameter(description = "Buyer id", required = true) @PathVariable UUID userId);

    @GetMapping("/products/followed/{userId}/sorted")
    ResponseEntity<FollowersPostsResponseDto> getSortedPostsByDate(
            @PathVariable UUID userId,
            @RequestParam(name = "order", required = false, defaultValue = "date_desc") String order);

    @Operation(summary = "Get latest posts from sellers - [REQ - 6]", description = "Returns the most recent posts from sellers followed by the given buyer. Only includes posts from the last two weeks, sorted by newest first.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request: buyer not found or the buyer is not following any sellers.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/products/promo-post/count")
    ResponseEntity<SellerPromoPostsCountResponseDto> getUserPromoPostCount(@RequestParam(name = "user_id") UUID userId);

    @GetMapping("/products/post/{postId}")
    ResponseEntity<PostResponseDto> getPost(@PathVariable UUID postId);

    @GetMapping("/users/{userId}/average-post-price")
    ResponseEntity<SellerAveragePrice> getAveragePrice(@PathVariable UUID userId);
}
