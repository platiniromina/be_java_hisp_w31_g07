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
import com.mercadolibre.be_java_hisp_w31_g07.util.GenericObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.util.PostFactory;
import com.mercadolibre.be_java_hisp_w31_g07.util.SellerFactory;
import com.mercadolibre.be_java_hisp_w31_g07.util.UserFactory;
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
    private ISellerService sellerService;

    @Mock
    private IProductService productService;

    @Mock
    private IUserService userService;

    @Mock
    private GenericObjectMapper mapper;

    @InjectMocks
    private PostService postService;

    private PostDto postDto;
    private Post post;
    private UUID postId;
    private PostResponseDto postResponseDto;
    private Seller seller;
    private UUID sellerId;

    @BeforeEach
    void setUp() {
        seller = SellerFactory.createSeller();
        sellerId = seller.getId();
        postDto = PostFactory.createPostDto(sellerId, false);
        post = PostFactory.createPost(sellerId, false);
        postId = post.getId();
        postResponseDto = PostFactory.createPostResponseDto(postId, sellerId, false);
    }

    @Test
    @DisplayName("[SUCCESS] Create post")
    void testCreatePostSuccess() {
        when(sellerService.findSellerById(sellerId)).thenReturn(seller);
        doNothing().when(productService).createProduct(post.getProduct());
        doNothing().when(postRepository).createPost(post);
        when(mapper.map(post, PostResponseDto.class)).thenReturn(postResponseDto);
        when(mapper.map(postDto, Post.class)).thenReturn(post);

        PostResponseDto result = postService.createPost(postDto);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(postResponseDto, result)
        );

        verify(sellerService).findSellerById(sellerId);
        verify(postRepository).createPost(post);
        verify(mapper).map(post, PostResponseDto.class);
        verifyNoMoreInteractions(postRepository, sellerService, mapper);
    }

    @Test
    @DisplayName("[ERROR] Create post - Seller not found")
    void testCreatePostError() {
        when(sellerService.findSellerById(sellerId)).thenThrow(
                new BadRequest("Seller " + sellerId + " not found")
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
    @DisplayName("[SUCCESS] Get Seller promo posts count")
    void testGetPromoPostsCountSuccess() {
        UserDto user = UserFactory.createUserDto();
        List<Post> promoPosts = List.of(
                PostFactory.createPost(sellerId, true),
                PostFactory.createPost(sellerId, true)
        );
        SellerPromoPostsCountResponseDto expected =
                new SellerPromoPostsCountResponseDto(sellerId, user.getUserName(), promoPosts.size());
        when(postRepository.findHasPromo(sellerId)).thenReturn(promoPosts);
        when(userService.findById(sellerId)).thenReturn(user);

        SellerPromoPostsCountResponseDto result = postService.getPromoPostsCount(sellerId);

        assertEquals(expected.getPromoPostsCount(), result.getPromoPostsCount());
        verify(postRepository).findHasPromo(sellerId);
        verify(userService).findById(sellerId);
        verifyNoMoreInteractions(postRepository, userService);
    }

    @Test
    @DisplayName("[ERROR] Get Seller promo posts count - Seller not found")
    void testGetPromoPostsCountError() {
        when(sellerService.findSellerById(sellerId)).thenThrow(
                new BadRequest("Seller " + sellerId + " not found")
        );

        Exception exception = assertThrows(BadRequest.class,
                () -> postService.getPromoPostsCount(sellerId));

        assertEquals("Seller " + sellerId + " not found", exception.getMessage());
        verify(sellerService).findSellerById(sellerId);
        verifyNoMoreInteractions(sellerService);
        verifyNoInteractions(postRepository);
        verifyNoInteractions(userService);
    }

}