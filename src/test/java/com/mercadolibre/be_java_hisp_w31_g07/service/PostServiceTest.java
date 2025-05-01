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
import com.mercadolibre.be_java_hisp_w31_g07.util.BuyerFactory;
import com.mercadolibre.be_java_hisp_w31_g07.util.GenericObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.util.PostFactory;
import com.mercadolibre.be_java_hisp_w31_g07.util.SellerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private IPostRepository postRepository;

    @Mock
    private ISellerService sellerService;

    @Mock
    private IBuyerService buyerService;

    @Mock
    private IProductService productService;

    @Mock
    private GenericObjectMapper mapper;

    @InjectMocks
    private PostService postService;

    private PostDto postDto;
    private Post post;
    private UUID postId;
    private PostResponseDto postResponseDto;
    private Seller seller;
    private Buyer buyer;
    private UUID buyerId;
    private BuyerDto buyerDto;

    @BeforeEach
    void setUp() {
        buyerDto = BuyerFactory.createBuyerDto();
        seller = SellerFactory.createSeller();
        buyer = BuyerFactory.createBuyer();
        buyerId = buyer.getId();
        postDto = PostFactory.createPostDto(seller.getId());
        post = PostFactory.createPost(seller.getId());
        postId = post.getId();
        postResponseDto = PostFactory.createPostResponseDto(seller.getId());
    }

    @Test
    @DisplayName("[SUCCESS] Create post")
    void testCreatePostSuccess() {
        when(sellerService.findSellerById(seller.getId())).thenReturn(seller);
        doNothing().when(productService).createProduct(post.getProduct());
        doNothing().when(postRepository).createPost(post);
        when(mapper.map(post, PostResponseDto.class)).thenReturn(postResponseDto);
        when(mapper.map(postDto, Post.class)).thenReturn(post);

        PostResponseDto result = postService.createPost(postDto);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(postResponseDto, result)
        );

        verify(sellerService).findSellerById(seller.getId());
        verify(postRepository).createPost(post);
        verify(mapper).map(post, PostResponseDto.class);
        verifyNoMoreInteractions(postRepository, sellerService, mapper);
    }

    @Test
    @DisplayName("[ERROR] Create post - Seller not found")
    void testCreatePostError() {
        when(sellerService.findSellerById(seller.getId())).thenThrow(
                new BadRequest("Seller " + seller.getId() + " not found")
        );

        assertThrows(BadRequest.class, () ->
                postService.createPost(postDto));

        verifyNoMoreInteractions(sellerService, postRepository, productService);
    }

    @Test
    @DisplayName("[SUCCESS] Find post - Success")
    void testFindPostSuccess() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(mapper.map(post, PostResponseDto.class)).thenReturn(postResponseDto);

        PostResponseDto result = postService.findPost(postId);

        assertEquals(postResponseDto, result);
        verify(postRepository).findById(postId);
        verify(mapper).map(post, PostResponseDto.class);
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
    @DisplayName("[SUCCESS] Get latest posts from from sellers")
    void testGetLatestPostsFromSellers() {

    }

    @Test
    @DisplayName("[SUCCESS] Get latest posts from from sellers - No posts")
    void testGetLatestPostsFromSellersButNoPostsMatchTheFilter() {

    }

    @Test
    @DisplayName("[ERROR] Get latest posts from from sellers - Buyer not found")
    void testGetLatestPostsFromSellersBuyerNotFound() {
        when(buyerService.findFollowed(buyerId)).thenThrow(new BadRequest("Buyer: " + buyerId + " not found"));
        Exception exception = assertThrows(BadRequest.class, () -> postService.getLatestPostsFromSellers(buyerId));
        assertEquals("Buyer: " + buyerId + " not found", exception.getMessage());
        verifyNoMoreInteractions(buyerService, postRepository);
    }

    @Test
    @DisplayName("[ERROR] Get latest posts from from sellers - Buyer is not following anyone")
    void testGetLatestPostsFromSellersBuyerNotFollowingAnyone() {
        when(buyerService.findFollowed(buyerId)).thenReturn(buyerDto);
        Exception exception = assertThrows(BadRequest.class, () -> postService.getLatestPostsFromSellers(buyerId));
        assertEquals("The buyer is not following any sellers", exception.getMessage());
        verifyNoMoreInteractions(buyerService, postRepository);
    }

}