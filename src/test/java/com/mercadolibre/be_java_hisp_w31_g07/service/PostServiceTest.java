package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerPromoPostsCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.UserPostResponseDto;
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
    private PostMapper mapper;

    @Mock
    private IPostBridgeService postBridgeService;

    @InjectMocks
    private PostService postService;

    private UUID sellerId;
    private UUID postId;
    private UUID userPostRespDtoId;

    private UserDto userSeller;

    private UserPostResponseDto userPostResponseDto;

    private Post post;
    private Post post2;
    private PostDto postDto;
    private PostResponseDto postResponseDto;

    @BeforeEach
    void setUp() {
        Seller seller = SellerFactory.createSeller(null);
        sellerId = seller.getId();
        userSeller = UserFactory.createUserDto(sellerId);

        userPostResponseDto = UserFactory.createUserPostResponseDto(sellerId);
        userPostRespDtoId = userPostResponseDto.getUserId();

        post = PostFactory.createPost(sellerId, false);
        postId = post.getId();
        post2 = PostFactory.createPost(sellerId, false);
        post2.setPrice(150.0);

        postDto = PostFactory.createPostDto(sellerId, false);
        postResponseDto = PostFactory.createPostResponseDto(postId, sellerId, false);
    }

    @Test
    @DisplayName("[SUCCESS] Create post")
    void testCreatePostSuccess() {
        doNothing().when(postBridgeService).validateSellerExists(sellerId);
        doNothing().when(postBridgeService).createProduct(post.getProduct());
        doNothing().when(postRepository).createPost(post);
        when(mapper.fromPostDtoToPost(postDto)).thenReturn(post);
        when(mapper.fromPostToPostResponseDto(post)).thenReturn(postResponseDto);

        PostResponseDto result = postService.createPost(postDto);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(postResponseDto, result)
        );

        verify(postBridgeService).validateSellerExists(sellerId);
        verify(postRepository).createPost(post);
        verify(postBridgeService).createProduct(post.getProduct());
        verify(mapper).fromPostToPostResponseDto(post);
        verifyNoMoreInteractions(postBridgeService, postRepository, mapper);
    }

    @Test
    @DisplayName("[ERROR] Create post - Seller not found")
    void testCreatePostError() {
        doThrow(new BadRequest(ErrorMessagesUtil.sellerNotFound(sellerId)))
                .when(postBridgeService).validateSellerExists(sellerId);

        assertThrows(BadRequest.class, () ->
                postService.createPost(postDto));

        verify(postBridgeService).validateSellerExists(sellerId);
        verifyNoMoreInteractions(postBridgeService, postRepository);
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

        assertEquals(ErrorMessagesUtil.postNotFound(postId), exception.getMessage());
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
        doNothing().when(postBridgeService).validateSellerExists(sellerId);
        when(postBridgeService.getUserName(sellerId)).thenReturn(userSeller.getUserName());

        SellerPromoPostsCountResponseDto result = postService.getPromoPostsCount(sellerId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expected.getUserId(), result.getUserId()),
                () -> assertEquals(expected.getUserName(), result.getUserName()),
                () -> assertEquals(expected.getPromoPostsCount(), result.getPromoPostsCount())
        );
        verify(postRepository).findHasPromo(sellerId);
        verify(postBridgeService).getUserName(sellerId);
        verify(postBridgeService).validateSellerExists(sellerId);
        verifyNoMoreInteractions(postRepository, postBridgeService);
    }

    @Test
    @DisplayName("[ERROR] Get Seller promo posts count - Seller not found")
    void testGetPromoPostsCountError() {
        doThrow(new BadRequest(ErrorMessagesUtil.sellerNotFound(sellerId)))
                .when(postBridgeService).validateSellerExists(sellerId);

        Exception exception = assertThrows(BadRequest.class,
                () -> postService.getPromoPostsCount(sellerId));

        assertEquals("Seller " + sellerId + " not found", exception.getMessage());
        verify(postBridgeService).validateSellerExists(sellerId);
        verifyNoMoreInteractions(postBridgeService);
        verifyNoInteractions(postRepository);
    }

    @Test
    @DisplayName("[SUCCESS] Find Promos posts - Success")
    void testGetSellerPromoPostsSuccess() {
        List<Post> promoPostList = List.of(post);
        List<PostResponseDto> postsListExpected = List.of(postResponseDto);

        userPostResponseDto.setPostList(postsListExpected);

        when(postRepository.findHasPromo(sellerId)).thenReturn(promoPostList);
        when(postBridgeService.getUserById(sellerId)).thenReturn(userSeller);
        doNothing().when(postBridgeService).validateSellerExists(sellerId);
        when(mapper.fromPostListToPostResponseDtoList(promoPostList)).thenReturn(postsListExpected);

        UserPostResponseDto result = postService.getSellerPromoPosts(sellerId);

        assertAll(
                () -> assertEquals(userPostResponseDto.getPostList().size(), result.getPostList().size()),
                () -> assertEquals(userPostRespDtoId, result.getUserId()),
                () -> assertEquals(userPostResponseDto.getUserName(), result.getUserName()),
                () -> assertNotNull(result.getPostList())
        );

        verify(postRepository).findHasPromo(sellerId);
        verify(postBridgeService).getUserById(sellerId);
        verify(postBridgeService).validateSellerExists(sellerId);
        verifyNoMoreInteractions(postBridgeService, postRepository);
    }

    @Test
    @DisplayName("[ERROR] Find Promos posts - Not Found Seller")
    void testGetSellerPromoPostsNotSellerFound() {
        doThrow(new BadRequest(ErrorMessagesUtil.sellerNotFound(sellerId)))
                .when(postBridgeService).validateSellerExists(sellerId);

        Exception exception = assertThrows(BadRequest.class,
                () -> postService.getSellerPromoPosts(sellerId));

        assertEquals("Seller " + sellerId + " not found", exception.getMessage());
        verify(postBridgeService).validateSellerExists(sellerId);
        verifyNoMoreInteractions(postBridgeService);
        verifyNoInteractions(postRepository);
    }

    @Test
    @DisplayName("[ERROR] Find Promos posts - Not Found Post Promotion List")
    void testGetSellerPromoPostsEmptyPostList() {
        when(postRepository.findHasPromo(userPostRespDtoId)).thenReturn(List.of());

        Exception exception = assertThrows(BadRequest.class,
                () -> postService.getSellerPromoPosts(sellerId));

        assertEquals("No promotional posts found for user: " + sellerId, exception.getMessage());
        verify(postRepository).findHasPromo(sellerId);
        verify(postBridgeService).validateSellerExists(sellerId);
        verifyNoMoreInteractions(postRepository);
        verifyNoMoreInteractions(postBridgeService);
    }

    @Test
    @DisplayName("[ERROR] Find Promos posts - Not Found User")
    void testGetSellerPromoPostsUserNotFound() {
        List<Post> promoPostList = List.of(post);
        List<PostResponseDto> postsListExpected = List.of(postResponseDto);

        userPostResponseDto.setPostList(postsListExpected);

        when(postRepository.findHasPromo(sellerId)).thenReturn(promoPostList);
        when(mapper.fromPostListToPostResponseDtoList(promoPostList)).thenReturn(postsListExpected);
        when(postBridgeService.getUserById(sellerId))
                .thenThrow(new BadRequest("User " + sellerId + " not found"));

        Exception exception = assertThrows(BadRequest.class,
                () -> postService.getSellerPromoPosts(sellerId));

        assertEquals("User " + sellerId + " not found", exception.getMessage());
        verify(postRepository).findHasPromo(sellerId);
        verify(postBridgeService).validateSellerExists(sellerId);
        verify(postBridgeService).getUserById(sellerId);
        verifyNoMoreInteractions(postRepository);
        verifyNoMoreInteractions(postBridgeService);
    }

    @Test
    @DisplayName("[SUCCESS] Find Promos posts - Success")
    void testFindAveragePrice() {
        List<Post> promoPostList = List.of(post, post2);
        Double averageExpected = (post.getPrice() + post2.getPrice()) / 2;

        when(postRepository.findPostsBySellerId(sellerId)).thenReturn(promoPostList);

        Double result = postService.findAveragePrice(sellerId);

        assertEquals(averageExpected, result);
        verify(postRepository).findPostsBySellerId(sellerId);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    @DisplayName("[ERROR] Find Promos posts - Not found Post")
    void testFindAveragePriceNotPostFound() {
        when(postRepository.findPostsBySellerId(sellerId)).thenReturn(List.of());

        Exception exception = assertThrows(BadRequest.class,
                () -> postService.findAveragePrice(sellerId)
        );

        assertEquals("User " + sellerId + " has no posts.", exception.getMessage());
        verify(postRepository).findPostsBySellerId(sellerId);
        verifyNoMoreInteractions(postRepository);
    }


}