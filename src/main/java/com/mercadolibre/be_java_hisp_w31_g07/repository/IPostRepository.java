package com.mercadolibre.be_java_hisp_w31_g07.repository;

import com.mercadolibre.be_java_hisp_w31_g07.model.Post;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IPostRepository {

    /**
     * Saves a post to the repository.
     *
     * @param post the post to be saved
     */
    void save(Post post);

    /**
     * Creates a new post, applying default values for promotion-related fields if necessary.
     *
     * @param post the post to be created
     */
    void createPost(Post post);

    /**
     * Finds a post by its unique identifier.
     *
     * @param postId the ID of the post
     * @return an Optional containing the post if found, otherwise empty
     */
    Optional<Post> findById(UUID postId);

    /**
     * Retrieves promotional posts for a specific seller.
     *
     * @param userId the seller's ID
     * @return a list of posts with promotions
     */
    List<Post> findHasPromo(UUID userId);

    /**
     * Retrieves the most recent posts (within the last 2 weeks) from a set of sellers.
     *
     * @param sellers a set of seller IDs
     * @return a list of the latest posts from those sellers
     */
    List<Post> findLatestPostsFromSellers(Set<UUID> sellers);

    /**
     * Retrieves all posts made by a specific seller.
     *
     * @param userId the seller's ID
     * @return a list of posts by the seller
     */
    List<Post> findPostsBySellerId(UUID userId);
}

