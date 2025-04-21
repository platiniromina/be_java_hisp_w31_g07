package com.mercadolibre.be_java_hisp_w31_g07.repository;

import com.mercadolibre.be_java_hisp_w31_g07.model.Post;

import java.util.Optional;
import java.util.UUID;

public interface IPostRepository {
    void createPost(Post post);

    Optional<Post> findById(UUID postId);
}
