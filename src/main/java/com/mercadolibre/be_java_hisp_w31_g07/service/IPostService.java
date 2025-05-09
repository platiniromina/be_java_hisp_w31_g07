package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.*;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;

import java.util.List;
import java.util.UUID;

public interface IPostService {
    /**
     * Allows a seller to create a new post.
     *
     * @param newPost {@link PostDto} contains all data to create a Post, except
     *                its UUID, which is generated by this service
     * @return created post converted to {@link PostResponseDto}
     * @throws BadRequest if the seller trying to create the post
     *                    does not exist
     */
    PostResponseDto createPost(PostDto newPost);

    /**
     * Find the user's promo posts
     *
     * @param userId contains the user id to link with a Post
     * @return list of Posts related with a user
     * @throws BadRequest if the seller doesn't have a post with discount
     */
    List<PostResponseDto> findUserPromoPosts(UUID userId);

    /**
     * Find average price of a user's post
     *
     * @param userId contains the user id to link with a Post.
     * @return average price related with a user.
     * @throws BadRequest if the seller doesn't have a post.
     */
    Double findAveragePrice(UUID userId);

    /**
     * Returns the most recent posts from sellers followed by the given buyer.
     * Only includes posts from the last two weeks, sorted by newest first.
     *
     * @param buyerId the unique identifier of the buyer.
     */
    FollowersPostsResponseDto getLatestPostsFromSellers(UUID buyerId);

    /**
     * Retrieves the count of promotional posts for a given seller.
     *
     * @param sellerId the unique identifier of the seller whose promotional
     *                 posts count is to be retrieved.
     * @return a {@link SellerPromoPostsCountResponseDto} containing the seller's
     * ID, username, and the count of promotional posts for the seller.
     * @throws BadRequest if the seller cannot be found.
     */
    SellerPromoPostsCountResponseDto getPromoPostsCount(UUID sellerId);

    /**
     * Retrieves a post by its unique identifier.
     *
     * @param postId the unique identifier of the post to be retrieved.
     * @return a {@link PostResponseDto} containing the post's details.
     * @throws BadRequest if the post with the given ID cannot be found.
     */
    PostResponseDto findPost(UUID postId);

    /**
     * Returns the posts from sellers followed by the given buyer, sorted by the
     * specified order.
     * The posts can be sorted either by date in ascending or descending order.
     *
     * @param buyerId the unique identifier of the buyer.
     * @param order   the sorting order for the posts. It can be "date_asc" for
     *                ascending order
     *                or "date_desc" for descending order. Defaults to "date_desc"
     *                if not provided.
     * @return a {@link FollowersPostsResponseDto} containing the sorted list of
     * posts from the sellers
     * followed by the buyer.
     * @throws IllegalArgumentException if the provided order is invalid.
     */
    FollowersPostsResponseDto sortPostsByDate(UUID buyerId, String order);
    
    /**
     * Retrieves a seller with a list of post with discount.
     *
     * @param userId the unique identifier of the seller to be retrieved
     * @return a UserPostReponseObject object containing the seller's information
     * and post.
     * @throws BadRequest if the seller cannot be found
     */
    UserPostResponseDto getSellerPromoPosts(UUID userId);

    /**
     * Calculates the average price of posts made by a specific seller and returns it along with basic user information.
     *
     * @param userId the ID of the seller whose post prices are to be averaged
     * @return a SellerAveragePrice object containing the seller's ID, username, and average post price
     */
    SellerAveragePriceDto findPricePerPosts(UUID userId);
}
