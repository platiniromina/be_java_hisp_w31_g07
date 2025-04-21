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
import com.mercadolibre.be_java_hisp_w31_g07.service.IProductService;
import com.mercadolibre.be_java_hisp_w31_g07.service.ISellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final IPostRepository postRepository;
    private final ISellerService sellerService;
    private final IProductService productService;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public UUID createPost(PostDto newPost) {

        // to convert PostDto from request to Post
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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
        productService.createProduct(post.getProduct());
        postRepository.createPost(post);

        return newPostId;
    }

    public Seller findSellerById(UUID sellerId) {
        return sellerService.findSellerById(sellerId);
    }

    @Override
    public PostResponseDto findPost(UUID postId) {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new NotFoundException("Post " + postId + " not found");
        }

        return mapper.convertValue(post.get(), PostResponseDto.class);
    }

}
