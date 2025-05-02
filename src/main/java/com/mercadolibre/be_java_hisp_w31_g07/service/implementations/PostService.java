package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IPostRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IPostService;
import com.mercadolibre.be_java_hisp_w31_g07.util.ErrorMessagesUtil;
import com.mercadolibre.be_java_hisp_w31_g07.util.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.mercadolibre.be_java_hisp_w31_g07.util.ValidationUtil.throwIfEmpty;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final IPostRepository postRepository;
    private final PostMapper postMapper;

    @Override
    public Post createPost(PostDto postDto) {
        Post post = postMapper.fromPostDtoToPost(postDto);
        postRepository.createPost(post);
        return post;
    }

    @Override
    public Post findPost(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BadRequest(ErrorMessagesUtil.postNotFound(postId)));
    }

    @Override
    public List<Post> findUserPromoPosts(UUID userId) {
        List<Post> posts = postRepository.findHasPromo(userId);
        throwIfEmpty(posts, "No promotional posts found for user: " + userId);
        return posts;
    }

    @Override
    public Double findAveragePriceBySellerId(UUID sellerId) {
        List<Post> posts = postRepository.findPostsBySellerId(sellerId);
        throwIfEmpty(posts, "User " + sellerId + " has no posts.");

        return posts.stream()
                .mapToDouble(this::getEffectivePrice)
                .average()
                .orElseThrow(() -> new BadRequest(ErrorMessagesUtil.noPurchasesForProduct(sellerId.toString())));
    }

    @Override
    public List<Post> findLatestPostsFromSellers(Set<UUID> ids) {
        return postRepository.findLatestPostsFromSellers(ids);
    }

    private double getEffectivePrice(Post post) {
        return Boolean.TRUE.equals(post.getHasPromo())
                ? post.getPrice() * (1 - post.getDiscount() / 100.0)
                : post.getPrice();
    }
}
