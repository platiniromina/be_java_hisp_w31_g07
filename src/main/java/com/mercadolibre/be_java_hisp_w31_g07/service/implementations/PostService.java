package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.model.Product;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IPostRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IProductRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final IPostRepository postRepository;
    private final ISellerRepository sellerRepository;
    private final IProductRepository productRepository;

    @Override
    public UUID createPost(PostDto newPost) {

        // to convert PostDto from request to Post
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());

        Post post;
        try {
            post = mapper.convertValue(newPost, Post.class);
        } catch (IllegalArgumentException e) {
            throw new BadRequest("Unable to parse the post");
        }

        // validate that seller exists
        Seller seller = findSellerById(newPost.getSellerId());

        // generate post
        UUID newPostId = UUID.randomUUID();
        post.setId(newPostId);
        post.getProduct().setId(newPostId);

        // create post and product in their repositories
        productRepository.createProduct(post.getProduct());
        postRepository.createPost(post);

        return newPostId;
    }

    public Seller findSellerById(UUID sellerId) {
        return sellerRepository.findSellerById(sellerId)
                .orElseThrow(() -> new BadRequest("Seller " + sellerId + " not found"));
    }

    @Override
    public PostResponseDto findPost(UUID postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new NotFoundException("Post " + postId + " not found");
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(post.get(), PostResponseDto.class);
    }

}
