package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IPostService {

    /**
     * Creates a new post in the repository.
     *
     * @param postDto the DTO containing post data
     * @return the created Post entity
     */
    Post createPost(PostDto postDto);

    /**
     * Finds a post by its ID.
     *
     * @param postId the ID of the post
     * @return the Post entity if found
     * @throws BadRequest if the post does not exist
     */
    Post findPost(UUID postId);

    /**
     * Finds promotional posts from a specific user.
     *
     * @param userId the ID of the user
     * @return a list of promotional posts
     * @throws BadRequest if no promotional posts are found
     */
    List<Post> findUserPromoPosts(UUID userId);

    /**
     * Calculates the average effective price of posts for a seller.
     *
     * @param sellerId the ID of the seller
     * @return the average price
     * @throws BadRequest if the seller has no posts or no valid prices
     */
    Double findAveragePriceBySellerId(UUID sellerId);

    /**
     * Finds the latest posts from a set of seller IDs.
     *
     * @param ids a set of seller IDs
     * @return a list of latest posts
     */
    List<Post> findLatestPostsFromSellers(Set<UUID> ids);
}
