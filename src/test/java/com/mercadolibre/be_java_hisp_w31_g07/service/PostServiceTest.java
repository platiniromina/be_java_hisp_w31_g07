package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IPostRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.implementations.PostService;
import com.mercadolibre.be_java_hisp_w31_g07.util.*;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.FollowersPostsResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerPromoPostsCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.util.PostFactory;
import com.mercadolibre.be_java_hisp_w31_g07.util.SellerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
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

    private UserDto userSeller;
    private Seller seller;
    private SellerResponseDto sellerResponseDto;

    private Buyer buyer;
    private BuyerDto buyerDto;

    private Post post;
    private PostDto postDto;
    private PostResponseDto postResponseDto;

    @BeforeEach
    void setUp() {
        seller = SellerFactory.createSeller(null);
        sellerId = seller.getId();
        userSeller = UserFactory.createUserDto(sellerId);
        sellerResponseDto = SellerFactory.createSellerResponseDto(sellerId);

        buyer = BuyerFactory.createBuyer(null);
        buyerId = buyer.getId();
        buyerDto = BuyerFactory.createBuyerDto();

        post = PostFactory.createPost(sellerId, false);
        postId = post.getId();
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
    @DisplayName("[SUCCESS] Get latest posts from from sellers")
    void testGetLatestPostsFromSellers() {

        buyer.setFollowed(List.of(seller));
        PostResponseDto expected = PostFactory.createPostResponseDto(sellerId, postId, false);

        when(postBridgeService.getFollowed(buyerId)).thenReturn(List.of(seller));
        when(postRepository.findLatestPostsFromSellers(List.of(sellerResponseDto.getId()))).thenReturn(List.of(post));
        when(mapper.fromPostListToPostResponseDtoList(List.of(post))).thenReturn(List.of(expected));

        FollowersPostsResponseDto result = postService.getLatestPostsFromSellers(buyerId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(buyerId, result.getId()),
                () -> assertEquals(1, result.getPosts().size()),
                () -> assertEquals(expected, result.getPosts().get(0))
        );

        verify(postBridgeService).getFollowed(buyerId);
        verify(postRepository).findLatestPostsFromSellers(List.of(sellerResponseDto.getId()));
        verify(mapper).fromPostListToPostResponseDtoList(List.of(post));
        verifyNoMoreInteractions(postBridgeService, postRepository, mapper);
    }

    @Test
    @DisplayName("[SUCCESS] Get latest posts from from sellers - No posts")
    void testGetLatestPostsFromSellersButNoPostsMatchTheFilter() {
        buyerDto.setFollowed(List.of(sellerResponseDto));
        when(postBridgeService.getFollowed(buyerId)).thenReturn(List.of(seller));
        when(postRepository.findLatestPostsFromSellers(List.of(sellerResponseDto.getId())))
                .thenReturn(Collections.emptyList());

        FollowersPostsResponseDto result = postService.getLatestPostsFromSellers(buyerId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(buyerId, result.getId()),
                () -> assertTrue(result.getPosts().isEmpty())
        );
        verify(postBridgeService).getFollowed(buyerId);
        verify(postRepository).findLatestPostsFromSellers(List.of(sellerResponseDto.getId()));
        verifyNoMoreInteractions(postBridgeService, postRepository);
    }

    @Test
    @DisplayName("[ERROR] Get latest posts from from sellers - Buyer not found")
    void testGetLatestPostsFromSellersBuyerNotFound() {
        when(postBridgeService.getFollowed(buyerId)).thenThrow(new BadRequest(ErrorMessagesUtil.buyerNotFound(buyerId)));

        Exception exception = assertThrows(BadRequest.class, () -> postService.getLatestPostsFromSellers(buyerId));

        assertEquals(ErrorMessagesUtil.buyerNotFound(buyerId), exception.getMessage());
        verify(postBridgeService).getFollowed(buyerId);
        verifyNoMoreInteractions(postBridgeService, postRepository);
    }

    @Test
    @DisplayName("[ERROR] Get latest posts from from sellers - Buyer is not following anyone")
    void testGetLatestPostsFromSellersBuyerNotFollowingAnyone() {
        when(postBridgeService.getFollowed(buyerId)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(BadRequest.class, () -> postService.getLatestPostsFromSellers(buyerId));

        assertEquals(ErrorMessagesUtil.noSellersFollowed(buyerId), exception.getMessage());
        verify(postBridgeService).getFollowed(buyerId);
        verifyNoMoreInteractions(postBridgeService, postRepository);
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