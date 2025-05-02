package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.*;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.model.Product;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.service.*;
import com.mercadolibre.be_java_hisp_w31_g07.util.PostMapper;
import com.mercadolibre.be_java_hisp_w31_g07.util.SortUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostOrchestrator implements IPostOrchestrator {

    private final IPostService postService;
    private final IUserService userService;
    private final ISellerService sellerService;
    private final IBuyerService buyerService;
    private final IProductService productService;
    private final PostMapper postMapper;

    @Override
    public PostResponseDto createPost(PostDto postDto) {
        validateSellerExists(postDto.getSellerId());
        Post post = executePostCreation(postDto);
        return postMapper.fromPostToPostResponseDto(post);
    }

    @Override
    public UserPostResponseDto findUserPromoPosts(UUID userId) {
        List<Post> postList = postService.findUserPromoPosts(userId);
        List<PostResponseDto> postResponseDtoList = postMapper.fromPostListToPostResponseDtoList(postList);
        UserDto user = userService.findById(userId);
        return new UserPostResponseDto(
                user.getId(),
                user.getUserName(),
                postResponseDtoList);
    }

    @Override
    public SellerPromoPostsCountResponseDto findPromoPostsCount(UUID userId) {
        validateSellerExists(userId);
        int count = postService.findUserPromoPosts(userId).size();
        UserDto user = userService.findById(userId);
        return new SellerPromoPostsCountResponseDto(
                user.getId(),
                user.getUserName(),
                count);
    }

    @Override
    public SellerAveragePrice findPricePerPostsBySellerId(UUID userId) {
        Double averagePrice = postService.findAveragePriceBySellerId(userId);
        UserDto user = userService.findById(userId);
        return new SellerAveragePrice(
                userId,
                user.getUserName(),
                averagePrice);
    }

    @Override
    public PostResponseDto findPost(UUID postId) {
        Post post = postService.findPost(postId);
        return postMapper.fromPostToPostResponseDto(post);
    }

    @Override
    public FollowersPostsResponseDto findLatestPostsFromSellers(UUID userId) {
        List<Seller> sellers = buyerService.findBuyerById(userId).getFollowed();
        Set<UUID> sellerIds = sellers.stream().map(Seller::getId).collect(Collectors.toSet());

        List<Post> posts = postService.findLatestPostsFromSellers(sellerIds);
        List<PostResponseDto> postDto = postMapper.fromPostListToPostResponseDtoList(posts);

        return new FollowersPostsResponseDto(userId, postDto);
    }

    @Override
    public FollowersPostsResponseDto findLatestPostsFromSellersSortedByDate(UUID buyerId, String order) {
        List<PostResponseDto> postDto = findLatestPostsFromSellers(buyerId).getPosts();
        List<PostResponseDto> sortedPosts = SortUtil.sortByDate(postDto, order);
        return new FollowersPostsResponseDto(buyerId, sortedPosts);
    }

    private void validateSellerExists(UUID sellerId) {
        sellerService.findSellerById(sellerId);
    }

    private Post executePostCreation(PostDto postDto) {
        Post post = postService.createPost(postDto);
        Product productToCreate = postMapper.fromProductDtoToProduct(postDto.getProduct());
        productService.createProduct(productToCreate);
        return post;
    }
}
