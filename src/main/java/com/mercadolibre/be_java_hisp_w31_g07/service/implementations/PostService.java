package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.FollowersPostsResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerPromoPostsCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IPostRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.*;
import com.mercadolibre.be_java_hisp_w31_g07.util.IdUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final IPostRepository postRepository;
    private final ISellerService sellerService;
    private final IProductService productService;
    private final IBuyerService buyerService;
    private final IUserService userService;
    private final ObjectMapper mapper;

    @Override
    public PostResponseDto createPost(PostDto newPost) {
        Post post = buildPostFromDto(newPost);
        savePostAndProduct(post);
        return mapper.convertValue(post, PostResponseDto.class);
    }

    @Override
    public List<PostResponseDto> findUserPromoPosts(UUID userId) {
        validateExistingSeller(userId);
        List<Post> postList = postRepository.findHasPromo(userId);

        if (postList.isEmpty()) {
            throw new BadRequest("Posts from: " + userId + " not found");
        }
        return postList.stream().map(post -> mapper.convertValue(post, PostResponseDto.class)).toList();
    }

    @Override
    public Double findAveragePrice(UUID userId) {
        return postRepository.findPricePerPosts(userId).stream().map(post -> {
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

        return new SellerPromoPostsCountResponseDto(
                sellerId,
                userService.findById(sellerId).getUserName(),
                promoPostsCount);
    }

    @Override
    public FollowersPostsResponseDto getLatestPostsFromSellers(UUID buyerId) {
        List<SellerResponseDto> sellers = buyerService.findFollowed(buyerId).getFollowed();

        if (sellers.isEmpty()) {
            throw new BadRequest("The buyer is not following any sellers");
        }

        List<UUID> sellerIds = sellers.stream().map(SellerResponseDto::getId).toList();
        List<Post> posts = postRepository.findLatestPostsFromSellers(sellerIds);

        List<PostResponseDto> postsDtos = posts.stream().map(post -> mapper.convertValue(post, PostResponseDto.class))
                .toList();
        return new FollowersPostsResponseDto(buyerId, postsDtos);
    }

    @Override
    public FollowersPostsResponseDto sortPostsByDate(UUID buyerId, String order) {
        FollowersPostsResponseDto postsResponse = getLatestPostsFromSellers(buyerId);
        List<PostResponseDto> sortedPosts = sortPosts(postsResponse.getPosts(), order);
        return new FollowersPostsResponseDto(buyerId, sortedPosts);
    }

    @Override
    public PostDto findProductByPurchase(String product) {
        return mapper.convertValue(postRepository.findProductByPurchase(product), PostDto.class);
    }

    @Override
    public PostResponseDto findPost(UUID postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequest("Post " + postId + " not found"));

        return mapper.convertValue(post, PostResponseDto.class);
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
        post.setGeneratedId(IdUtils.generateId());

        return post;
    }

    private void savePostAndProduct(Post post) {
        productService.createProduct(post.getProduct());
        postRepository.createPost(post);
    }

    private List<PostResponseDto> sortPosts(List<PostResponseDto> posts, String order) {
        Comparator<PostResponseDto> comparator = switch (order.toLowerCase()) {
            case "date_asc" -> Comparator.comparing(PostResponseDto::getDate);
            case "date_desc" -> Comparator.comparing(PostResponseDto::getDate).reversed();
            default -> throw new BadRequest("Invalid sorting parameter: " + order
                    + ", please try again with a valid one (date_asc or date_desc)");
        };

        return posts.stream()
                .sorted(comparator)
                .toList();

    }
}
