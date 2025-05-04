package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.FollowersPostsResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IBuyerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IPostRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.implementations.PostBridgeService;
import com.mercadolibre.be_java_hisp_w31_g07.service.implementations.PostService;
import com.mercadolibre.be_java_hisp_w31_g07.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private IPostRepository postRepository;

    @Mock
    private IBuyerRepository buyerRepository;

    @Mock
    private PostBridgeService bridgeService;

    @Mock
    private PostMapper mapper;

    @Mock
    private PostService postService;

    private PostDto postDto;
    private Post post;
    private Post post2;
    private UUID postId;
    private UUID postId2;
    private PostResponseDto postResponseDto;
    private PostResponseDto postResponseDto2;
    private UUID sellerId;
    private Buyer buyer;
    private Seller seller;
    private UUID buyerId;

    @BeforeEach
    void setUp() {
        seller = SellerFactory.createSeller();
        sellerId = seller.getId();

        buyer = BuyerFactory.createBuyer();
        buyer.setFollowed(List.of(seller));
        buyerId = buyer.getId();

        post = PostFactory.createPost(sellerId, false, LocalDateTime.now());
        postId = post.getId();

        post2 = PostFactory.createPost(sellerId, false, LocalDateTime.now().minusDays(1));
        postId2 = post2.getId();

        postResponseDto = PostFactory.createPostResponseDto(sellerId, postId, false);
        postResponseDto2 = PostFactory.createPostResponseDto(sellerId, postId2, false);
    }
    @Test
    @DisplayName("[SUCCESS] Sort post Asc")
    void testSortPostsByDate_asc() {
        when(postService.getLatestPostsFromSellers(buyerId))
                .thenReturn(new FollowersPostsResponseDto(buyerId, List.of(postResponseDto2, postResponseDto)));

        when(postService.sortPostsByDate(buyerId, "date_asc")).thenCallRealMethod();

        FollowersPostsResponseDto result = postService.sortPostsByDate(buyerId, "date_asc");

        assertEquals(postId2, result.getPosts().get(0).getId(), "Error: El primer post no es el más antiguo.");
        assertEquals(postId, result.getPosts().get(1).getId(), "Error: El segundo post no es el más reciente.");
    }
    @Test
    @DisplayName("[SUCCESS] Sort post Desc")
    void testSortPostsByDate_desc() {
        when(postService.getLatestPostsFromSellers(buyerId))
                .thenReturn(new FollowersPostsResponseDto(buyerId, List.of(postResponseDto, postResponseDto2)));

        when(postService.sortPostsByDate(buyerId, "date_desc")).thenCallRealMethod();

        FollowersPostsResponseDto result = postService.sortPostsByDate(buyerId, "date_desc");

        assertEquals(postId, result.getPosts().get(0).getId(), "Error: El primer post no es el más reciente.");
        assertEquals(postId2, result.getPosts().get(1).getId(), "Error: El segundo post no es el más antiguo.");
    }
    @Test
    @DisplayName("[FAIL] Sort posts with invalid order throws BadRequest exception")
    void testSortPostsByDate_invalidOrder_throwsException() {
        String invalidOrder = "invalid_order";

        when(postService.getLatestPostsFromSellers(buyerId))
                .thenReturn(new FollowersPostsResponseDto(buyerId, List.of(postResponseDto, postResponseDto2)));

        when(postService.sortPostsByDate(buyerId, invalidOrder)).thenCallRealMethod();

        BadRequest exception = assertThrows(BadRequest.class, () ->
                postService.sortPostsByDate(buyerId, invalidOrder)
        );

        assertEquals("Invalid sorting parameter: invalid_order", exception.getMessage());
    }


    @Test
    @DisplayName("[SUCCESS] Create post")
    void testCreatePostSuccess() {
        doNothing().when(bridgeService).validateSellerExists(sellerId);
        doNothing().when(bridgeService).createProduct(post.getProduct());
        doNothing().when(postRepository).createPost(post);
        when(mapper.fromPostDtoToPost(postDto)).thenReturn(post);
        when(mapper.fromPostToPostResponseDto(post)).thenReturn(postResponseDto);

        PostResponseDto result = postService.createPost(postDto);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(postResponseDto, result)
        );

        verify(bridgeService).validateSellerExists(sellerId);
        verify(postRepository).createPost(post);
        verify(bridgeService).createProduct(post.getProduct());
        verify(mapper).fromPostToPostResponseDto(post);
        verifyNoMoreInteractions(bridgeService, postRepository, mapper);
    }

    @Test
    @DisplayName("[ERROR] Create post - Seller not found")
    void testCreatePostError() {
        doThrow(new BadRequest(ErrorMessagesUtil.sellerNotFound(sellerId)))
                .when(bridgeService).validateSellerExists(sellerId);

        assertThrows(BadRequest.class, () ->
                postService.createPost(postDto));

        verifyNoMoreInteractions(bridgeService, postRepository);
    }

    @Test
    @DisplayName("[SUCCESS] Find post - Success")
    void testFindPostSuccess() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(mapper.fromPostToPostResponseDto(post)).thenReturn(postResponseDto);

        PostResponseDto result = postService.findPost(postId);

        assertEquals(postResponseDto, result);
        verify(postRepository).findById(postId);
        verify(mapper).fromPostToPostResponseDto(post);
        verifyNoMoreInteractions(postRepository, mapper);
    }

    @Test
    @DisplayName("[ERROR] Find post - Not Found")
    void testFindPostNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(BadRequest.class, () -> postService.findPost(postId));

        assertEquals("Post " + postId + " not found", exception.getMessage());
        verify(postRepository).findById(postId);
        verifyNoMoreInteractions(postRepository);
    }

}