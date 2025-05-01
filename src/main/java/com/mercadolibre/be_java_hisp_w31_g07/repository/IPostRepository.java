package com.mercadolibre.be_java_hisp_w31_g07.repository;

import com.mercadolibre.be_java_hisp_w31_g07.model.Post;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPostRepository {

    /**
     * Adds a new post to the repository.
     * <p>
     * This method is responsible for storing the given {@link Post} instance
     * in the repository. The post is added to an internal list of posts,
     * making it accessible for future retrievals.
     *
     * @param post the {@link Post} instance to be created and stored in the
     *             repository.
     */
    void createPost(Post post);

    /**
     * Retrieves a post by its unique identifier.
     * <p>
     * This method searches for a {@link Post} in the repository using the
     * provided UUID. If a post with the specified ID is found, it is returned
     * wrapped in an {@link Optional}. If no post is found, the method returns an
     * empty {@link Optional}.
     *
     * @param postId the unique identifier of the {@link Post} to be retrieved.
     * @return an {@link Optional} containing the found {@link Post}, or an empty
     * {@link Optional} if no post with the specified ID exists.
     */
    Optional<Post> findById(UUID postId);

    /**
     * Retrieves a list of posts that have promotional content for a specified user.
     * <p>
     * This method filters the internal list of posts to find all posts that
     * have promotions enabled ({@code hasPromo} set to {@code true}) and are
     * associated with the provided seller identified by the given UUID.
     * It returns a list containing only the posts that match these criteria.
     *
     * @param userId the unique identifier of the seller whose posts with promotions
     *               are to be retrieved.
     * @return a {@link List} of {@link Post} objects that have promotions and
     * belong
     * to the specified seller; may be empty if no such posts exist.
     */
    List<Post> findHasPromo(UUID userId);

    /**
     * Retrieves all posts matching the sellers ids within the last two weeks.
     * Sorts by newest posts first.
     *
     * @param sellers the unique identifier of the sellers.
     */
    List<Post> findLatestPostsFromSellers(List<UUID> sellers);

    /**
     * Retrieves a list of posts associated with a specific seller ID.
     *
     * @param userId the UUID of the seller whose posts are to be retrieved
     * @return a list of {@link Post} objects that belong to the specified seller
     */
    List<Post> findPostsBySellerId(UUID userId);

    /**
     * Finds a post containing a product by its name.
     *
     * @param product the name of the product to search for (case-insensitive).
     * @return an {@link Optional} containing the {@link Post} if a match is found,
     * or an empty {@link Optional} if no match is found.
     */
    Optional<Post> findProductByPurchase(String product);

    /**
     * Saves a post to the repository.
     *
     * @param post the {@link Post} instance to be saved.
     */
    void save(Post post);
}
