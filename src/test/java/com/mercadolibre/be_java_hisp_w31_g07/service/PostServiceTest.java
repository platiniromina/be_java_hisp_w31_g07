package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.*;
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
    private UUID userPostRespDtoId;

    private Seller seller;
    private Buyer buyer;

    private Post post;
    private Post post2;

    private PostDto postDto;
    private PostResponseDto postResponseDto;
    private PostResponseDto postResponseDto2;

    private UserDto userSeller;
    private SellerResponseDto sellerResponseDto;
    private UserPostResponseDto userPostResponseDto;

    private BuyerDto buyerDto;

    @BeforeEach
    void setUp() {
        seller = SellerFactory.createSeller(null);
        sellerId = seller.getId();
        userSeller = UserFactory.createUserDto(sellerId);
        sellerResponseDto = SellerFactory.createSellerResponseDto(sellerId);

        userPostResponseDto = UserFactory.createUserPostResponseDto(sellerId);
        userPostRespDtoId = userPostResponseDto.getUserId();

        buyer = BuyerFactory.createBuyer(null);
        buyerId = buyer.getId();
        buyerDto = BuyerFactory.createBuyerDto();

        seller.addFollower(buyer);
        buyer.addFollowedSeller(seller);

        post = PostFactory.createPost(sellerId, false);
        postId = post.getId();

        post2 = PostFactory.createPost(sellerId, false);
        post2.setPrice(150.0);

        postDto = PostFactory.createPostDto(sellerId, false);
        postResponseDto = PostFactory.createPostResponseDto(sellerId, postId, false);
        postResponseDto2 = PostFactory.createPostResponseDto(sellerId, post2.getId(), false);
    }


    @Test
    @DisplayName("[SUCCESS] Sort post Asc")
    void testSortPostsByDatAsc() {
        when(postBridgeService.getFollowed(buyerId)).thenReturn(List.of(seller));
        when(postRepository.findLatestPostsFromSellers(List.of(sellerId)))
                .thenReturn(List.of(post2, post));

        when(mapper.fromPostListToPostResponseDtoList(List.of(post2, post)))
                .thenReturn(List.of(postResponseDto2, postResponseDto));

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

        when(mapper.fromPostListToPostResponseDtoList(List.of(post, post2)))
                .thenReturn(List.of(postResponseDto, postResponseDto2));

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

        assertEquals(ErrorMessagesUtil.buyerIsNotFollowingAnySellers(buyerId), exception.getMessage());
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

        assertEquals(ErrorMessagesUtil.sellerNotFound(sellerId), exception.getMessage());
        verify(postBridgeService).validateSellerExists(sellerId);
        verifyNoMoreInteractions(postBridgeService);
        verifyNoInteractions(postRepository);
    }

    @Test
    @DisplayName("[ERROR] Find Promos posts - Not Found Post Promotion List")
    void testGetSellerPromoPostsEmptyPostList() {
        when(postRepository.findHasPromo(sellerId)).thenReturn(List.of());

        Exception exception = assertThrows(BadRequest.class,
                () -> postService.getSellerPromoPosts(sellerId));


        assertEquals(ErrorMessagesUtil.noPromotionalPostUser(sellerId), exception.getMessage());
        verify(postRepository).findHasPromo(sellerId);
        verify(postBridgeService).validateSellerExists(sellerId);
        verifyNoMoreInteractions(postRepository, postBridgeService);
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
                .thenThrow(new BadRequest(ErrorMessagesUtil.userNotFound(sellerId)));

        Exception exception = assertThrows(BadRequest.class,
                () -> postService.getSellerPromoPosts(sellerId));

        assertEquals(ErrorMessagesUtil.userNotFound(sellerId), exception.getMessage());
        verify(postRepository).findHasPromo(sellerId);
        verify(postBridgeService).validateSellerExists(sellerId);
        verify(postBridgeService).getUserById(sellerId);
        verifyNoMoreInteractions(postRepository, postBridgeService);
    }

    @Test
    @DisplayName("[SUCCESS] Find average price by seller - Success")
    void testFindAveragePrice() {
        List<Post> promoPostList = List.of(post, post2);
        Double averageExpected = (post.getPrice() + post2.getPrice()) / 2;

        doNothing().when(postBridgeService).validateSellerExists(sellerId);
        when(postRepository.findPostsBySellerId(sellerId)).thenReturn(promoPostList);

        Double result = postService.findAveragePrice(sellerId);

        assertEquals(averageExpected, result);
        verify(postRepository).findPostsBySellerId(sellerId);
        verify(postBridgeService).validateSellerExists(sellerId);
        verifyNoMoreInteractions(postRepository, postBridgeService);
    }

    @Test
    @DisplayName("[ERROR] Find average price by seller - Not found Post")
    void testFindAveragePriceNotPostFound() {
        when(postRepository.findPostsBySellerId(sellerId)).thenReturn(List.of());

        Exception exception = assertThrows(BadRequest.class,
                () -> postService.findAveragePrice(sellerId)
        );

        assertEquals(ErrorMessagesUtil.userHasNotPosts(sellerId), exception.getMessage());
        verify(postRepository).findPostsBySellerId(sellerId);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    @DisplayName("[SUCCESS] Find price per post")
    void testFindPricePerPosts() {
        List<Post> promoPostList = List.of(post, post2);
        UserDto userDto = UserFactory.createUserDto(sellerId);

        Double averageExpected = (post.getPrice() + post2.getPrice()) / 2;

        doNothing().when(postBridgeService).validateSellerExists(sellerId);
        when(postRepository.findPostsBySellerId(sellerId)).thenReturn(promoPostList);
        when(postBridgeService.getUserById(sellerId)).thenReturn(userDto);

        SellerAveragePriceDto sellerExpected = new SellerAveragePriceDto(
                userDto.getId(),
                userDto.getUserName(),
                averageExpected
        );

        SellerAveragePriceDto result = postService.findPricePerPosts(sellerId);

        assertAll(
                () -> assertEquals(sellerExpected.getIdSeller(), result.getIdSeller()),
                () -> assertEquals(sellerExpected.getAveragePrice(), result.getAveragePrice()),
                () -> assertEquals(sellerExpected.getUserName(), result.getUserName())
        );

        verify(postBridgeService).validateSellerExists(sellerId);
        verify(postBridgeService).getUserById(sellerId);
        verify(postRepository).findPostsBySellerId(sellerId);
        verifyNoMoreInteractions(postBridgeService, postRepository);
    }

    @Test
    @DisplayName("[ERROR] Find price per post - User not found")
    void testFindPricePerPostsUserNotFound() {
        List<Post> promoPostList = List.of(post, post2);

        doNothing().when(postBridgeService).validateSellerExists(sellerId);
        when(postRepository.findPostsBySellerId(sellerId)).thenReturn(promoPostList);
        when(postBridgeService.getUserById(sellerId)).thenThrow(
                new BadRequest(ErrorMessagesUtil.userNotFound(sellerId))
        );

        Exception exception = assertThrows(BadRequest.class,
                () -> postService.findPricePerPosts(sellerId)
        );

        assertEquals(ErrorMessagesUtil.userNotFound(sellerId), exception.getMessage());

        verify(postBridgeService).validateSellerExists(sellerId);
        verify(postRepository).findPostsBySellerId(sellerId);
        verify(postBridgeService).getUserById(sellerId);
        verifyNoMoreInteractions(postBridgeService, postRepository);
    }

}