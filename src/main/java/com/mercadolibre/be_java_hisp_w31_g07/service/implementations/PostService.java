package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.FollowersPostsResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerPromoPostsCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IPostRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.*;
import com.mercadolibre.be_java_hisp_w31_g07.util.GenericObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.util.IdUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final IPostRepository postRepository;
    private final ObjectProvider<ISellerService> sellerServiceProvider;
    private final ObjectProvider<IProductService> productServiceProvider;
    private final ObjectProvider<IBuyerService> buyerServiceProvider;
    private final IUserService userService;
    private final GenericObjectMapper mapper;

    // ------------------------------
    // Public methods
    // ------------------------------

    @Override
    public PostResponseDto createPost(PostDto newPost) {
        Post post = buildPostFromDto(newPost);
        savePostAndProduct(post);
        return mapper.map(post, PostResponseDto.class);
    }

    @Override
    public PostResponseDto findPost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequest("Post " + postId + " not found"));

        return mapper.map(post, PostResponseDto.class);
    }

    @Override
    public List<PostResponseDto> findUserPromoPosts(UUID userId) {
        validateExistingSeller(userId);
        List<Post> postList = getPromoPostsOrThrow(userId);
        return mapPostsToDto(postList);
    }

    @Override
    public Double findAveragePrice(UUID userId) {
        return postRepository.findPostsBySellerId(userId).stream().map(post -> {
                    double finalPrice = post.getPrice();
                    if ((post.getHasPromo())) {
                        post.setPrice(finalPrice * (1 - post.getDiscount() / 100.0));
                    }
                    return post;
                })
                .mapToDouble(Post::getPrice)
                .average().orElseThrow(() -> new BadRequest("User " + userId + " has no posts."));
    }

    @Override
    public SellerPromoPostsCountResponseDto getPromoPostsCount(UUID sellerId) {
        validateExistingSeller(sellerId);
        Integer promoPostsCount = postRepository.findHasPromo(sellerId).size();
        String sellerName = userService.findById(sellerId).getUserName();

        return new SellerPromoPostsCountResponseDto(
                sellerId,
                sellerName,
                promoPostsCount);
    }

    @Override
    public FollowersPostsResponseDto getLatestPostsFromSellers(UUID buyerId) {
        List<UUID> followedSellerIds = getFollowedSellerIdsOrThrow(buyerId);
        List<Post> posts = postRepository.findLatestPostsFromSellers(followedSellerIds);
        List<PostResponseDto> postDtos = mapPostsToDto(posts);
        return new FollowersPostsResponseDto(buyerId, postDtos);
    }

    @Override
    public FollowersPostsResponseDto sortPostsByDate(UUID buyerId, String order) {
        FollowersPostsResponseDto postsResponse = getLatestPostsFromSellers(buyerId);
        List<PostResponseDto> sortedPosts = sortPosts(postsResponse.getPosts(), order);
        return new FollowersPostsResponseDto(buyerId, sortedPosts);
    }

    @Override
    public PostDto findProductByPurchase(String product) {
        Post post = postRepository.findProductByPurchase(product)
                .orElseThrow(() -> new BadRequest("No purchase found for product: " + product));
        return mapper.map(post, PostDto.class);
    }

    // ------------------------------
    // Private methods
    // ------------------------------

    private void savePostAndProduct(Post post) {
        productServiceProvider.getObject().createProduct(post.getProduct());
        postRepository.createPost(post);
    }

    private List<PostResponseDto> sortPosts(List<PostResponseDto> posts, String order) {
        return switch (order.toLowerCase()) {
            case "date_desc" -> posts.stream()
                    .sorted(Comparator.comparing(PostResponseDto::getDate).reversed())
                    .toList();
            case "date_asc" -> posts.stream()
                    .sorted(Comparator.comparing(PostResponseDto::getDate))
                    .toList();
            default -> throw new BadRequest("Invalid sorting parameter: " + order +
                    ". Please use 'date_asc' or 'date_desc'.");
        };
    }

    // ------------------------------
    // Validation Methods
    // ------------------------------

    private void validateExistingSeller(UUID sellerId) {
        sellerServiceProvider.getObject().findSellerById(sellerId);
    }

    // ------------------------------
    // Data Fetching Methods
    // ------------------------------

    private List<UUID> getFollowedSellerIdsOrThrow(UUID buyerId) {
        List<SellerResponseDto> sellers = buyerServiceProvider.getObject().findFollowed(buyerId).getFollowed();

        if (sellers.isEmpty()) {
            throw new BadRequest("The buyer is not following any sellers");
        }

        return sellers.stream()
                .map(SellerResponseDto::getId)
                .toList();
    }

    private List<Post> getPromoPostsOrThrow(UUID userId) {
        List<Post> posts = postRepository.findHasPromo(userId);

        if (posts.isEmpty()) {
            throw new BadRequest("No promotional posts found for user: " + userId);
        }
        return posts;
    }

    // ------------------------------
    // Data Transformation Methods
    // ------------------------------

    private Post buildPostFromDto(PostDto dto) {
        validateExistingSeller(dto.getSellerId());
        Post post = mapper.map(dto, Post.class);
        post.setGeneratedId(IdUtils.generateId());
        return post;
    }

    private List<PostResponseDto> mapPostsToDto(List<Post> posts) {
        return mapper.mapList(posts, PostResponseDto.class);
    }
}
