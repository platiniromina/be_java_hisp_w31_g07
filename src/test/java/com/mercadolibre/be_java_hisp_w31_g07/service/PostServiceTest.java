package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.FollowersPostsResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerPromoPostsCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
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
    private UUID buyerId;
    private UUID postId;
    private UUID postId2;

    private Seller seller;
    private Buyer buyer;

    private Post post;
    private Post post2;

    private PostDto postDto;

    private PostResponseDto postResponseDto;
    private PostResponseDto postResponseDto2;

    private UserDto userSeller;

    @BeforeEach
    void setUp() {
        seller = SellerFactory.createSeller(null);
        sellerId = seller.getId();
        userSeller = UserFactory.createUserDto(sellerId);

        buyer = BuyerFactory.createBuyer(null);
        buyerId = buyer.getId();

        seller.addFollower(buyer);
        buyer.addFollowedSeller(seller);


        post = PostFactory.createPost(sellerId, false);
        post2 = PostFactory.createPost(sellerId, false);
        postId = post.getId();
        postId2 = post2.getId();

        postDto = PostFactory.createPostDto(sellerId, false);
        postResponseDto = PostFactory.createPostResponseDto(sellerId, postId, false);
        postResponseDto2 = PostFactory.createPostResponseDto(sellerId, postId2, false);
    }
    @Test
    @DisplayName("[SUCCESS] Sort post Asc")
    void testSortPostsByDatAsc() {
        when(postBridgeService.getFollowed(buyerId)).thenReturn(List.of(seller));
        when(postRepository.findLatestPostsFromSellers(List.of(sellerId)))
                .thenReturn(List.of(post2, post));

        doReturn(List.of(postResponseDto2, postResponseDto))
                .when(mapper).fromPostListToPostResponseDtoList(List.of(post2, post)); // Mantén el mismo orden

        FollowersPostsResponseDto result = postService.sortPostsByDate(buyerId, "date_asc");

        assertEquals(List.of(postResponseDto2, postResponseDto), result.getPosts());

        verify(postBridgeService).getFollowed(buyerId);
        verify(postRepository).findLatestPostsFromSellers(List.of(sellerId));
        verify(mapper).fromPostListToPostResponseDtoList(List.of(post2, post));
        verifyNoMoreInteractions(postBridgeService, postRepository, mapper);
    }
    @Test
    @DisplayName("[SUCCESS] Sort post Desc")
    void testSortPostsByDateDesc() {
        when(postBridgeService.getFollowed(buyerId)).thenReturn(List.of(seller));
        when(postRepository.findLatestPostsFromSellers(List.of(sellerId)))
                .thenReturn(List.of(post, post2));

        doReturn(List.of(postResponseDto, postResponseDto2))
                .when(mapper).fromPostListToPostResponseDtoList(List.of(post, post2));

        FollowersPostsResponseDto result = postService.sortPostsByDate(buyerId, "date_desc");

        assertEquals(List.of(postResponseDto, postResponseDto2), result.getPosts());

        verify(postBridgeService).getFollowed(buyerId);
        verify(postRepository).findLatestPostsFromSellers(List.of(sellerId));
        verify(mapper).fromPostListToPostResponseDtoList(List.of(post, post2));
        verifyNoMoreInteractions(postBridgeService, postRepository, mapper);
    }

    @Test
    @DisplayName("[ERROR] Sort posts with invalid order throws BadRequest exception")
    void testSortPostsByDateInvalidOrderThrowsException() {
        String invalidOrder = "invalid_order";

        when(postBridgeService.getFollowed(buyerId)).thenReturn(List.of(seller));
        when(postRepository.findLatestPostsFromSellers(List.of(sellerId)))
                .thenReturn(List.of(post, post2));
        when(mapper.fromPostListToPostResponseDtoList(List.of(post, post2)))
                .thenReturn(List.of(postResponseDto, postResponseDto2));

        BadRequest exception = assertThrows(BadRequest.class, () ->
                postService.sortPostsByDate(buyerId, invalidOrder)
        );

        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(ErrorMessagesUtil.invalidSortingParameter(invalidOrder), exception.getMessage())
        );

        verify(postBridgeService).getFollowed(buyerId);
        verify(postRepository).findLatestPostsFromSellers(List.of(sellerId));
        verify(mapper).fromPostListToPostResponseDtoList(List.of(post, post2));
        verifyNoMoreInteractions(postBridgeService, postRepository, mapper);
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

}