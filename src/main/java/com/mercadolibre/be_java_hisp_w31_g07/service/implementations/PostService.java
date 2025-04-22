package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.FollowersPostsResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IPostRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IBuyerService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IPostService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IProductService;
import com.mercadolibre.be_java_hisp_w31_g07.service.ISellerService;
import com.mercadolibre.be_java_hisp_w31_g07.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final IPostRepository postRepository;
    private final ISellerService sellerService;
    private final IProductService productService;
    private final IBuyerService buyerService;
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
    public PostResponseDto findPost(UUID postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post " + postId + " not found"));

        return mapper.convertValue(post, PostResponseDto.class);
    }

    @Override
    public List<PostResponseDto> findUserPromoPosts(UUID userId) {
        List<Post> postList = postRepository.findHasPromo(userId);

        if (postList.isEmpty()) {
            throw new NotFoundException("Posts from: " + userId + " not found");
        }
        return postList.stream().map(post -> mapper.convertValue(post, PostResponseDto.class)).toList();
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
}
