package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.*;

import java.util.UUID;

public interface IPostOrchestrator {

    /**
     * Creates a new post with the associated product and returns the response DTO.
     *
     * @param postDto the post data to create
     * @return the created post as a PostResponseDto
     */
    PostResponseDto createPost(PostDto postDto);

    /**
     * Retrieves the promotional posts made by a specific user.
     *
     * @param userId the ID of the user
     * @return a UserPostResponseDto containing user information and the list of promo posts
     */
    UserPostResponseDto findUserPromoPosts(UUID userId);

    /**
     * Retrieves the number of promotional posts for a specific seller.
     *
     * @param userId the ID of the seller
     * @return a response DTO with seller info and promo post count
     */
    SellerPromoPostsCountResponseDto findPromoPostsCount(UUID userId);

    /**
     * Retrieves the average price of the posts made by a specific seller.
     *
     * @param userId the ID of the seller
     * @return a DTO with seller ID, name, and average price
     */
    SellerAveragePrice findPricePerPostsBySellerId(UUID userId);

    /**
     * Retrieves a single post by its ID.
     *
     * @param postId the ID of the post
     * @return a PostResponseDto representing the found post
     */
    PostResponseDto findPost(UUID postId);

    /**
     * Retrieves the latest posts from sellers followed by the specified buyer.
     *
     * @param userId the ID of the buyer
     * @return a DTO with the list of posts from followed sellers
     */
    FollowersPostsResponseDto findLatestPostsFromSellers(UUID userId);

    /**
     * Retrieves and sorts by date the latest posts from sellers followed by the specified buyer.
     *
     * @param buyerId the ID of the buyer
     * @param order   sorting order, e.g., "asc" or "desc"
     * @return a DTO with the sorted list of posts
     */
    FollowersPostsResponseDto findLatestPostsFromSellersSortedByDate(UUID buyerId, String order);
}

