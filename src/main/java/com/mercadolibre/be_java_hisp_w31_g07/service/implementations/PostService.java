package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.*;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IPostRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IPostBridgeService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IPostService;
import com.mercadolibre.be_java_hisp_w31_g07.util.ErrorMessagesUtil;
import com.mercadolibre.be_java_hisp_w31_g07.util.PostMapper;
import com.mercadolibre.be_java_hisp_w31_g07.util.SortUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import static com.mercadolibre.be_java_hisp_w31_g07.util.ValidationUtil.throwIfEmpty;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final IPostRepository postRepository;
    private final IPostBridgeService postBridgeService;
    private final PostMapper postMapper;

    // ------------------------------
    // Public methods
    // ------------------------------

    @Override
    public PostResponseDto createPost(PostDto newPost) {
        postBridgeService.validateSellerExists(newPost.getSellerId());
        Post post = postMapper.fromPostDtoToPost(newPost);
        savePostAndProduct(post);
        return postMapper.fromPostToPostResponseDto(post);
    }

    @Override
    public PostResponseDto findPost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequest(ErrorMessagesUtil.postNotFound(postId)));
        return postMapper.fromPostToPostResponseDto(post);
    }

    @Override
    public List<PostResponseDto> findUserPromoPosts(UUID userId) {
        postBridgeService.validateSellerExists(userId);
        return postMapper.fromPostListToPostResponseDtoList(getPromoPostsOrThrow(userId));
    }

    @Override
    public UserPostResponseDto getSellerPromoPosts(UUID sellerId) {
        List<PostResponseDto> userPromoPosts = findUserPromoPosts(sellerId);
        UserDto user = postBridgeService.getUserById(sellerId);

        return new UserPostResponseDto(
                user.getId(),
                user.getUserName(),
                userPromoPosts);
    }

    @Override
    public Double findAveragePrice(UUID sellerId) {
        postBridgeService.validateSellerExists(sellerId);

        List<Post> posts = postRepository.findPostsBySellerId(sellerId);
        throwIfEmpty(posts, ErrorMessagesUtil.userHasNotPosts(sellerId));

        double average = posts.stream()
                .mapToDouble(this::getEffectivePrice)
                .average()
                .orElseThrow(() -> new BadRequest(ErrorMessagesUtil.noPurchasesForProduct(sellerId.toString())));

        BigDecimal roundedAverage = BigDecimal.valueOf(average)
                .setScale(1, RoundingMode.HALF_UP);

        return roundedAverage.doubleValue();
    }


    @Override
    public SellerPromoPostsCountResponseDto getPromoPostsCount(UUID sellerId) {
        postBridgeService.validateSellerExists(sellerId);
        Integer promoPostsCount = postRepository.findHasPromo(sellerId).size();
        String sellerName = postBridgeService.getUserName(sellerId);

        return new SellerPromoPostsCountResponseDto(
                sellerId,
                sellerName,
                promoPostsCount);
    }

    @Override
    public FollowersPostsResponseDto getLatestPostsFromSellers(UUID buyerId) {
        List<UUID> followedSellerIds = getFollowedSellerIdsOrThrow(buyerId);
        List<Post> posts = postRepository.findLatestPostsFromSellers(followedSellerIds);
        List<PostResponseDto> postDto = postMapper.fromPostListToPostResponseDtoList(posts);
        return new FollowersPostsResponseDto(buyerId, postDto);
    }

    @Override
    public FollowersPostsResponseDto sortPostsByDate(UUID buyerId, String order) {
        FollowersPostsResponseDto postsResponse = getLatestPostsFromSellers(buyerId);
        List<PostResponseDto> sortedPosts = SortUtil.sortByDate(postsResponse.getPosts(), order);
        return new FollowersPostsResponseDto(buyerId, sortedPosts);
    }


    @Override
    public PostDto findProductByPurchase(String product) {
        Post post = postRepository.findProductByPurchase(product)
                .orElseThrow(() -> new BadRequest(ErrorMessagesUtil.noPurchasesForProduct(product)));
        return postMapper.fromPostToPostDto(post);
    }


    @Override
    public SellerAveragePrice findPricePerPosts(UUID userId) {
        Double averagePrice = findAveragePrice(userId);
        UserDto user = postBridgeService.getUserById(userId);
        return new SellerAveragePrice(
                userId,
                user.getUserName(),
                averagePrice);
    }

    // ------------------------------
    // Private methods
    // ------------------------------

    private void savePostAndProduct(Post post) {
        postBridgeService.createProduct(post.getProduct());
        postRepository.createPost(post);
    }

    private double getEffectivePrice(Post post) {
        return Boolean.TRUE.equals(post.getHasPromo())
                ? post.getPrice() * (1 - post.getDiscount() / 100.0)
                : post.getPrice();
    }

    private List<UUID> getFollowedSellerIdsOrThrow(UUID buyerId) {
        List<Seller> sellers = postBridgeService.getFollowed(buyerId);
        throwIfEmpty(sellers, ErrorMessagesUtil.buyerIsNotFollowingAnySellers(buyerId));
        return sellers.stream().map(Seller::getId).toList();
    }

    private List<Post> getPromoPostsOrThrow(UUID userId) {
        List<Post> posts = postRepository.findHasPromo(userId);
        throwIfEmpty(posts, ErrorMessagesUtil.noPromoPostsFoundForSeller(userId));
        return posts;
    }

}
