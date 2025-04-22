package com.mercadolibre.be_java_hisp_w31_g07.repository;

import com.mercadolibre.be_java_hisp_w31_g07.model.Post;

import java.util.Optional;
import java.util.UUID;

public interface IPostRepository {

    /**
     * Adds a new post to the repository.
     *
     * This method is responsible for storing the given {@link Post} instance
     * in the repository. The post is added to an internal list of posts,
     * making it accessible for future retrievals.
     *
     * @param post the {@link Post} instance to be created and stored in the repository.
     */
    public void createPost(Post post);


    /**
     * Retrieves a post by its unique identifier.
     *
     * This method searches for a {@link Post} in the repository using the
     * provided UUID. If a post with the specified ID is found, it is returned
     * wrapped in an {@link Optional}. If no post is found, the method returns an
     * empty {@link Optional}.
     *
     * @param postId the unique identifier of the {@link Post} to be retrieved.
     * @return an {@link Optional} containing the found {@link Post}, or an empty
     *         {@link Optional} if no post with the specified ID exists.
     */
    public Optional<Post> findById(UUID postId);
}
