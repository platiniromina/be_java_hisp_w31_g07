package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IPostRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IPostService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IProductService;
import com.mercadolibre.be_java_hisp_w31_g07.service.ISellerService;
import com.mercadolibre.be_java_hisp_w31_g07.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final IPostRepository postRepository;
    private final ISellerService sellerService;
    private final IProductService productService;

    private final ObjectMapper mapper;

    @Override
    public PostResponseDto createPost(PostDto newPost) {

        // to convert PostDto from request to Post
        Post post = mapper.convertValue(newPost, Post.class);

        // validate that seller exists
        validateExistingSeller(newPost.getSellerId());

        // generate UUID for the new post
        UUID newPostId = Utils.generateId();
        post.setGeneratedId(newPostId);

        // create post and product in their repositories
        productService.createProduct(post.getProduct());
        postRepository.createPost(post);

        return mapper.convertValue(post, PostResponseDto.class);
    }

    private void validateExistingSeller(UUID sellerId) {
        sellerService.findSellerById(sellerId);
    }

    @Override
    public PostResponseDto findPost(UUID postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post " + postId + " not found"));

        return mapper.convertValue(post, PostResponseDto.class);
    }

}
