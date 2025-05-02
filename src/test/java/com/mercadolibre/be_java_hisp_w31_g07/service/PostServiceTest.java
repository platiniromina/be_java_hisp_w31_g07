package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerPromoPostsCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
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
    private IPostBridgeService bridgeService;

    @Mock
    private PostMapper mapper;

    @InjectMocks
    private PostService postService;

    private PostDto postDto;
    private Post post;
    private UUID postId;
    private PostResponseDto postResponseDto;
    private UserDto userSeller;
    private UUID sellerId;

    @BeforeEach
    void setUp() {
        Seller seller = SellerFactory.createSeller();
        sellerId = seller.getId();

        post = PostFactory.createPost(sellerId, false);
        postId = post.getId();

        postDto = PostFactory.createPostDto(sellerId, false);
        postResponseDto = PostFactory.createPostResponseDto(postId, sellerId, false);

        userSeller = UserFactory.createUserDto(sellerId);
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

    @Test
    @DisplayName("[SUCCESS] Get Seller promo posts count")
    void testGetPromoPostsCountSuccess() {
        List<Post> promoPosts = List.of(
                PostFactory.createPost(sellerId, true),
                PostFactory.createPost(sellerId, true)
        );
        SellerPromoPostsCountResponseDto expected =
                new SellerPromoPostsCountResponseDto(sellerId, userSeller.getUserName(), promoPosts.size());
        when(postRepository.findHasPromo(sellerId)).thenReturn(promoPosts);
        doNothing().when(bridgeService).validateSellerExists(sellerId);
        when(bridgeService.getUserName(sellerId)).thenReturn(userSeller.getUserName());

        SellerPromoPostsCountResponseDto result = postService.getPromoPostsCount(sellerId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expected.getUserId(), result.getUserId()),
                () -> assertEquals(expected.getUserName(), result.getUserName()),
                () -> assertEquals(expected.getPromoPostsCount(), result.getPromoPostsCount())
        );
        verify(postRepository).findHasPromo(sellerId);
        verify(bridgeService).getUserName(sellerId);
        verify(bridgeService).validateSellerExists(sellerId);
        verifyNoMoreInteractions(postRepository, bridgeService);
    }

    @Test
    @DisplayName("[ERROR] Get Seller promo posts count - Seller not found")
    void testGetPromoPostsCountError() {
        doThrow(new BadRequest(ErrorMessagesUtil.sellerNotFound(sellerId)))
                .when(bridgeService).validateSellerExists(sellerId);

        Exception exception = assertThrows(BadRequest.class,
                () -> postService.getPromoPostsCount(sellerId));

        assertEquals("Seller " + sellerId + " not found", exception.getMessage());
        verify(bridgeService).validateSellerExists(sellerId);
        verifyNoMoreInteractions(bridgeService);
        verifyNoInteractions(postRepository);
    }

}