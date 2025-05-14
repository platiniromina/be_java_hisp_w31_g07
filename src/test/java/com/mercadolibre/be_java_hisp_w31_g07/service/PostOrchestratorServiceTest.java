package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.*;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.service.implementations.PostOrchestrator;
import com.mercadolibre.be_java_hisp_w31_g07.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostOrchestratorServiceTest {

    @Mock
    private IPostService postService;

    @Mock
    private ISellerService sellerService;

    @Mock
    private IProductService productService;

    @Mock
    private PostMapper mapper;

    @Mock
    private IBuyerService buyerService;

    @Mock
    private IUserService userService;

    @InjectMocks
    private PostOrchestrator postOrchestrator;

    private UUID sellerId;
    private UUID buyerId;
    private UUID postId;

    private Seller seller;
    private Buyer buyer;

    private Post post;

    private PostDto postDto;
    private PostResponseDto postResponseDto;

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
        postId = post.getId();

        Post post2 = PostFactory.createPost(sellerId, false);
        post2.setPrice(150.0);

        postDto = PostFactory.createPostDto(sellerId, false);
        postResponseDto = PostFactory.createPostResponseDto(sellerId, postId, false);
    }


    @Test
    @DisplayName("[SUCCESS] Create post")
    void testCreatePostSuccess() {
        when(sellerService.findSellerById(sellerId)).thenReturn(seller);
        when(postService.createPost(postDto)).thenReturn(post);
        when(mapper.fromProductDtoToProduct(postDto.getProduct())).thenReturn(post.getProduct());
        doNothing().when(productService).createProduct(post.getProduct());
        when(mapper.fromPostToPostResponseDto(post)).thenReturn(postResponseDto);

        PostResponseDto result = postOrchestrator.createPost(postDto);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(postResponseDto, result)
        );
        verify(sellerService).findSellerById(sellerId);
        verify(postService).createPost(postDto);
        verify(mapper).fromProductDtoToProduct(postDto.getProduct());
        verify(productService).createProduct(post.getProduct());
        verify(mapper).fromPostToPostResponseDto(post);
        verifyNoMoreInteractions(postService, productService, mapper);
    }

    @Test
    @DisplayName("[ERROR] Create post - Seller not found")
    void testCreatePostError() {
        doThrow(new BadRequest(ErrorMessagesUtil.sellerNotFound(sellerId)))
                .when(sellerService).findSellerById(sellerId);

        Exception exception = assertThrows(BadRequest.class, () ->
                postOrchestrator.createPost(postDto));

        assertEquals(ErrorMessagesUtil.sellerNotFound(sellerId), exception.getMessage());
        verifyNoMoreInteractions(sellerService, postService, productService, mapper);
    }

    @Test
    @DisplayName("[SUCCESS] Find post - Success")
    void testFindPostSuccess() {
        when(postService.findPost(postId)).thenReturn(post);
        when(mapper.fromPostToPostResponseDto(post)).thenReturn(postResponseDto);

        PostResponseDto result = postOrchestrator.findPost(postId);

        assertEquals(postResponseDto, result);
        verify(postService).findPost(postId);
        verify(mapper).fromPostToPostResponseDto(post);
        verifyNoMoreInteractions(postService, mapper);
    }

    @Test
    @DisplayName("[ERROR] Find post - Not Found")
    void testFindPostNotFound() {
        when(postService.findPost(postId)).thenThrow(new BadRequest(ErrorMessagesUtil.postNotFound(postId)));

        Exception exception = assertThrows(BadRequest.class, () -> postService.findPost(postId));

        assertEquals(ErrorMessagesUtil.postNotFound(postId), exception.getMessage());
        verify(postService).findPost(postId);
        verifyNoMoreInteractions(postService);
    }

    @Test
    @DisplayName("[SUCCESS] Get latest posts from followed sellers")
    void testFindLatestPostsFromSellers() {
        buyer.setFollowed(List.of(seller));
        List<Seller> followedSellers = buyer.getFollowed();
        Set<UUID> sellerIds = followedSellers.stream().map(Seller::getId).collect(Collectors.toSet());
        List<Post> posts = List.of(post);
        List<PostResponseDto> postDtos = List.of(PostFactory.createPostResponseDto(sellerId, postId, false));

        when(buyerService.findBuyerById(buyerId)).thenReturn(buyer);
        when(postService.findLatestPostsFromSellers(sellerIds)).thenReturn(posts);
        when(mapper.fromPostListToPostResponseDtoList(posts)).thenReturn(postDtos);


        FollowersPostsResponseDto result = postOrchestrator.findLatestPostsFromSellers(buyerId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(buyerId, result.getId()),
                () -> assertEquals(1, result.getPosts().size()),
                () -> assertEquals(postDtos.get(0), result.getPosts().get(0))
        );

        verify(buyerService).findBuyerById(buyerId);
        verify(postService).findLatestPostsFromSellers(sellerIds);
        verify(mapper).fromPostListToPostResponseDtoList(posts);
        verifyNoMoreInteractions(buyerService, postService, mapper);
    }

    @Test
    @DisplayName("[SUCCESS] Get latest posts from followed sellers - No posts")
    void testFindLatestPostsFromSellersButNoPostsMatchTheFilter() {
        buyer.setFollowed(List.of(seller));
        Set<UUID> sellerIds = buyer.getFollowed().stream().map(Seller::getId).collect(Collectors.toSet());

        // Stubbing
        when(buyerService.findBuyerById(buyerId)).thenReturn(buyer);
        when(postService.findLatestPostsFromSellers(sellerIds)).thenReturn(List.of());
        when(mapper.fromPostListToPostResponseDtoList(List.of())).thenReturn(List.of());

        FollowersPostsResponseDto result = postOrchestrator.findLatestPostsFromSellers(buyerId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(buyerId, result.getId()),
                () -> assertTrue(result.getPosts().isEmpty())
        );

        verify(buyerService).findBuyerById(buyerId);
        verify(postService).findLatestPostsFromSellers(sellerIds);
        verify(mapper).fromPostListToPostResponseDtoList(Collections.emptyList());
        verifyNoMoreInteractions(buyerService, postService, mapper);
    }

    @Test
    @DisplayName("[ERROR] Get latest posts from followed sellers - Buyer not found")
    void testFindLatestPostsFromSellersBuyerNotFound() {
        when(buyerService.findBuyerById(buyerId))
                .thenThrow(new BadRequest(ErrorMessagesUtil.buyerNotFound(buyerId)));

        Exception exception = assertThrows(BadRequest.class, () -> postOrchestrator.findLatestPostsFromSellers(buyerId));

        assertEquals(ErrorMessagesUtil.buyerNotFound(buyerId), exception.getMessage());

        verify(buyerService).findBuyerById(buyerId);
        verifyNoMoreInteractions(buyerService, postService, mapper);
    }

    @Test
    @DisplayName("[ERROR] Get latest posts from followed sellers - Buyer is not following anyone")
    void testFindLatestPostsFromSellersBuyerNotFollowingAnyone() {
        buyer.setFollowed(Collections.emptyList());

        when(buyerService.findBuyerById(buyerId)).thenReturn(buyer);

        Exception exception = assertThrows(BadRequest.class, () -> postOrchestrator.findLatestPostsFromSellers(buyerId));

        assertEquals(ErrorMessagesUtil.buyerIsNotFollowingAnySellers(buyerId), exception.getMessage());
        verify(buyerService).findBuyerById(buyerId);
        verifyNoMoreInteractions(buyerService, postService, mapper);
    }

    @Test
    @DisplayName("[SUCCESS] Get Seller promo posts count")
    void testFindPromoPostsCountSuccess() {
        List<Post> promoPosts = List.of(
                PostFactory.createPost(sellerId, true),
                PostFactory.createPost(sellerId, true)
        );

        SellerPromoPostsCountResponseDto expected =
                new SellerPromoPostsCountResponseDto(sellerId, userSeller.getUserName(), promoPosts.size());

        when(sellerService.findSellerById(sellerId)).thenReturn(seller);
        when(postService.findUserPromoPosts(sellerId)).thenReturn(promoPosts);
        when(userService.findById(sellerId)).thenReturn(userSeller);

        SellerPromoPostsCountResponseDto result = postOrchestrator.findPromoPostsCount(sellerId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expected.getUserId(), result.getUserId()),
                () -> assertEquals(expected.getUserName(), result.getUserName()),
                () -> assertEquals(expected.getPromoPostsCount(), result.getPromoPostsCount())
        );

        verify(sellerService).findSellerById(sellerId);
        verify(postService).findUserPromoPosts(sellerId);
        verify(userService).findById(sellerId);
        verifyNoMoreInteractions(sellerService, postService, userService);
    }

    @Test
    @DisplayName("[ERROR] Get Seller promo posts count - Seller not found")
    void testFindPromoPostsCountSellerNotFound() {
        when(sellerService.findSellerById(sellerId))
                .thenThrow(new BadRequest(ErrorMessagesUtil.sellerNotFound(sellerId)));

        Exception exception = assertThrows(BadRequest.class,
                () -> postOrchestrator.findPromoPostsCount(sellerId));

        assertEquals(ErrorMessagesUtil.sellerNotFound(sellerId), exception.getMessage());

        verify(sellerService).findSellerById(sellerId);
        verifyNoMoreInteractions(sellerService);
        verifyNoInteractions(postService, userService);
    }

    @Test
    @DisplayName("[SUCCESS] Find Promos posts - Success")
    void testFindUserPromoPostsSuccess() {
        List<Post> promoPostList = List.of(post);
        List<PostResponseDto> postResponseDtoList = List.of(postResponseDto);

        when(postService.findUserPromoPosts(sellerId)).thenReturn(promoPostList);
        when(mapper.fromPostListToPostResponseDtoList(promoPostList)).thenReturn(postResponseDtoList);
        when(userService.findById(sellerId)).thenReturn(userSeller);

        UserPostResponseDto result = postOrchestrator.findUserPromoPosts(sellerId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(sellerId, result.getUserId()),
                () -> assertEquals(userSeller.getUserName(), result.getUserName()),
                () -> assertEquals(postResponseDtoList, result.getPostList())
        );

        verify(postService).findUserPromoPosts(sellerId);
        verify(mapper).fromPostListToPostResponseDtoList(promoPostList);
        verify(userService).findById(sellerId);
        verifyNoMoreInteractions(postService, mapper, userService);
    }

    @Test
    @DisplayName("[ERROR] Find Promos posts - Seller not found")
    void testFindUserPromoPostsSellerNotFound() {
        when(postService.findUserPromoPosts(sellerId))
                .thenThrow(new BadRequest(ErrorMessagesUtil.sellerNotFound(sellerId)));

        Exception exception = assertThrows(BadRequest.class,
                () -> postOrchestrator.findUserPromoPosts(sellerId));

        assertEquals(ErrorMessagesUtil.sellerNotFound(sellerId), exception.getMessage());

        verify(postService).findUserPromoPosts(sellerId);
        verifyNoMoreInteractions(postService);
    }

    @Test
    @DisplayName("[ERROR] Find Promos posts - No promo posts found")
    void testFindUserPromoPostsEmptyList() {
        doThrow(new BadRequest(ErrorMessagesUtil.noPromoPostForUser(sellerId)))
                .when(postService).findUserPromoPosts(sellerId);

        Exception exception = assertThrows(BadRequest.class,
                () -> postOrchestrator.findUserPromoPosts(sellerId));

        assertEquals(ErrorMessagesUtil.noPromoPostForUser(sellerId), exception.getMessage());

        verify(postService).findUserPromoPosts(sellerId);
        verifyNoMoreInteractions(postService);
    }

    @Test
    @DisplayName("[ERROR] Find Promos posts - User not found")
    void testFindUserPromoPostsUserNotFound() {
        when(userService.findById(sellerId))
                .thenThrow(new BadRequest(ErrorMessagesUtil.userNotFound(sellerId)));

        Exception exception = assertThrows(BadRequest.class,
                () -> postOrchestrator.findUserPromoPosts(sellerId));

        assertEquals(ErrorMessagesUtil.userNotFound(sellerId), exception.getMessage());

        verify(userService).findById(sellerId);
        verifyNoMoreInteractions(postService, mapper, userService);
    }


    @Test
    @DisplayName("[SUCCESS] Find average price by seller - Success")
    void testFindPricePerPostsBySellerIdSuccess() {
        Double averagePrice = 150.0;

        when(postService.findAveragePriceBySellerId(sellerId)).thenReturn(averagePrice);
        when(userService.findById(sellerId)).thenReturn(userSeller);

        SellerAveragePriceDto result = postOrchestrator.findPricePerPostsBySellerId(sellerId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(sellerId, result.getIdSeller()),
                () -> assertEquals(userSeller.getUserName(), result.getUserName()),
                () -> assertEquals(averagePrice, result.getAveragePrice())
        );

        verify(postService).findAveragePriceBySellerId(sellerId);
        verify(userService).findById(sellerId);
        verifyNoMoreInteractions(postService, userService);
    }


    @Test
    @DisplayName("[ERROR] Find average price by seller - User has no posts")
    void testFindPricePerPostsBySellerIdNoPosts() {
        when(postService.findAveragePriceBySellerId(sellerId))
                .thenThrow(new BadRequest(ErrorMessagesUtil.userHasNotPosts(sellerId)));

        Exception exception = assertThrows(BadRequest.class,
                () -> postOrchestrator.findPricePerPostsBySellerId(sellerId));

        assertEquals(ErrorMessagesUtil.userHasNotPosts(sellerId), exception.getMessage());

        verify(postService).findAveragePriceBySellerId(sellerId);
        verifyNoMoreInteractions(postService);
    }

}