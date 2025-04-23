package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.FollowersPostsResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerPromoPostsCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IPostRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.*;
import com.mercadolibre.be_java_hisp_w31_g07.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final IPostRepository postRepository;
    private final ISellerService sellerService;
    private final IProductService productService;
    private final IBuyerService buyerService;
    private final IUserService userService;
    private final ObjectMapper mapper;

    // ------------------------------
    // Public methods
    // ------------------------------

    @Override
    public PostResponseDto createPost(PostDto newPost) {
        Post post = buildPostFromDto(newPost);
        savePostAndProduct(post);
        return mapper.convertValue(post, PostResponseDto.class);
    }

    @Override
    public SellerPromoPostsCountResponseDto getPromoPostsCount(UUID sellerId) {
        validateExistingSeller(sellerId);
        Integer promoPostsCount = postRepository.findHasPromo(sellerId).size();

        return new SellerPromoPostsCountResponseDto(
                sellerId,
                userService.findById(sellerId).getUserName(),
                promoPostsCount
        );
    }

    @Override
    public List<PostResponseDto> findUserPromoPosts(UUID userId) {
        validateExistingSeller(userId);
        List<Post> postList = postRepository.findHasPromo(userId);

        if (postList.isEmpty()) {
            throw new NotFoundException("No promotional posts from user: " + userId + " were found");
        }
        return postList.stream()
                .map(post -> mapper.convertValue(post, PostResponseDto.class)).toList();
    }

    // ------------------------------
    // Private methods
    // ------------------------------

    private void validateExistingSeller(UUID sellerId) {
        sellerService.findSellerById(sellerId);
    }

    private Post buildPostFromDto(PostDto dto) {
        validateExistingSeller(dto.getSellerId());

        Post post = mapper.convertValue(dto, Post.class);
        post.setGeneratedId(Utils.generateId());

        return post;
    }

    private void savePostAndProduct(Post post) {
        productService.createProduct(post.getProduct());
        postRepository.createPost(post);
    }

    @Override
    public FollowersPostsResponseDto getLatestPostsFromSellers(UUID buyerId) {
        List<SellerResponseDto> sellers = buyerService.findFollowed(buyerId).getFollowed();

        if (sellers.isEmpty()) {
            throw new NotFoundException("The buyer is not following any sellers");
        }

        List<UUID> sellerIds = sellers.stream().map(SellerResponseDto::getId).toList();
        List<Post> posts = postRepository.findLatestPostsFromSellers(sellerIds);

        List<PostResponseDto> postsDtos = posts.stream().map(post -> mapper.convertValue(post, PostResponseDto.class))
                .toList();
        return new FollowersPostsResponseDto(buyerId, postsDtos);
    }

    // ------------------------------ START TESTING ------------------------------
    // FOR TESTING PURPOSES ONLY
    // This endpoint is not part of the original requirements
    // and is only used to verify the functionality of the followSeller method.

    // It should be removed in the final version of the code.

    @Override
    public PostResponseDto findPost(UUID postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post " + postId + " not found"));

        return mapper.convertValue(post, PostResponseDto.class);
    }
    // ------------------------------ END TESTING ------------------------------
}
