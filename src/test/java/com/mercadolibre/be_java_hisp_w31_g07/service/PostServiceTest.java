package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.model.User;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IPostRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.implementations.PostService;
import com.mercadolibre.be_java_hisp_w31_g07.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private IPostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    private PostDto postDto;
    private Post post;
    private UUID postId;
    private UUID userSellerId;
    private UUID sellerId;

    @BeforeEach
    void setUp() {
        User userSeller = UserFactory.createUser(null);
        userSellerId = userSeller.getId();
        Seller seller = SellerFactory.createSeller(userSellerId);
        sellerId = seller.getId();

        postDto = PostFactory.createPostDto(sellerId, true);
        post = PostFactory.createPost(sellerId, true);
        postId = post.getId();
        post.setPrice(200.0);
        post.setDiscount(25.0);
    }

    @Test
    @DisplayName("[SUCCESS] createPost")
    void testCreatePostSuccess() {
        when(postMapper.fromPostDtoToPost(postDto)).thenReturn(post);

        Post result = postService.createPost(postDto);

        assertEquals(post, result);
        verify(postRepository).createPost(post);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    @DisplayName("[SUCCESS] findPost - Found")
    void testFindPostSuccess() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post result = postService.findPost(postId);

        assertEquals(post, result);
        verify(postRepository).findById(postId);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    @DisplayName("[ERROR] findPost - Not Found")
    void testFindPostNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        BadRequest exception = assertThrows(BadRequest.class, () ->
                postService.findPost(postId));

        assertEquals(ErrorMessagesUtil.postNotFound(postId), exception.getMessage());
        verify(postRepository).findById(postId);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    @DisplayName("[SUCCESS] findUserPromoPosts - Found")
    void testFindUserPromoPostsSuccess() {
        List<Post> posts = List.of(post);
        when(postRepository.findHasPromo(userSellerId)).thenReturn(posts);

        List<Post> result = postService.findUserPromoPosts(userSellerId);

        assertEquals(posts, result);
        verify(postRepository).findHasPromo(userSellerId);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    @DisplayName("[ERROR] findUserPromoPosts - Empty List")
    void testFindUserPromoPostsEmpty() {
        when(postRepository.findHasPromo(userSellerId)).thenReturn(List.of());

        BadRequest exception = assertThrows(BadRequest.class, () ->
                postService.findUserPromoPosts(userSellerId));

        assertEquals(ErrorMessagesUtil.noPromoPostForUser(userSellerId), exception.getMessage());
        verify(postRepository).findHasPromo(userSellerId);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    @DisplayName("[SUCCESS] findAveragePriceBySellerId")
    void testFindAveragePriceBySellerId() {
        Post post2 = PostFactory.createPost(sellerId, true);
        post2.setPrice(100.0);
        post2.setDiscount(0.0);

        List<Post> posts = List.of(post, post2);
        when(postRepository.findPostsBySellerId(sellerId)).thenReturn(posts);

        Double result = postService.findAveragePriceBySellerId(sellerId);

        double expectedAverage = ((200 * 0.75) + 100) / 2.0;
        BigDecimal expectedRounded = BigDecimal.valueOf(expectedAverage).setScale(1, RoundingMode.HALF_UP);

        assertEquals(expectedRounded.doubleValue(), result);
        verify(postRepository).findPostsBySellerId(sellerId);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    @DisplayName("[ERROR] findAveragePriceBySellerId - No posts")
    void testFindAveragePriceBySellerIdNoPosts() {
        when(postRepository.findPostsBySellerId(sellerId)).thenReturn(List.of());

        BadRequest exception = assertThrows(BadRequest.class, () ->
                postService.findAveragePriceBySellerId(sellerId));

        assertEquals(ErrorMessagesUtil.userHasNotPosts(sellerId), exception.getMessage());
        verify(postRepository).findPostsBySellerId(sellerId);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    @DisplayName("[ERROR] findAveragePriceBySellerId - No average")
    void testFindAveragePriceBySellerIdNoAverage() {
        List<Post> posts = List.of();
        when(postRepository.findPostsBySellerId(sellerId)).thenReturn(posts);

        BadRequest exception = assertThrows(BadRequest.class, () ->
                postService.findAveragePriceBySellerId(sellerId));

        assertEquals(ErrorMessagesUtil.userHasNotPosts(sellerId), exception.getMessage());
        verify(postRepository).findPostsBySellerId(sellerId);
        verifyNoMoreInteractions(postRepository);

    }

    @Test
    @DisplayName("[SUCCESS] findLatestPostsFromSellers")
    void testFindLatestPostsFromSellers() {
        Set<UUID> sellerIds = Set.of(sellerId);
        List<Post> posts = List.of(post);

        when(postRepository.findLatestPostsFromSellers(sellerIds)).thenReturn(posts);

        List<Post> result = postService.findLatestPostsFromSellers(sellerIds);

        assertEquals(posts, result);
        verify(postRepository).findLatestPostsFromSellers(sellerIds);
        verifyNoMoreInteractions(postRepository);
    }
}
