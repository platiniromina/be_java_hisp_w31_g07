package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.service.implementations.PostOrchestrator;
import com.mercadolibre.be_java_hisp_w31_g07.util.ErrorMessagesUtil;
import com.mercadolibre.be_java_hisp_w31_g07.util.PostFactory;
import com.mercadolibre.be_java_hisp_w31_g07.util.PostMapper;
import com.mercadolibre.be_java_hisp_w31_g07.util.SellerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private IPostService postService;

    @Mock
    private ISellerService sellerService;

    @Mock
    private IProductService productService;

    @Mock
    private PostMapper mapper;

    @InjectMocks
    private PostOrchestrator postOrchestrator;

    private PostDto postDto;
    private Post post;
    private UUID postId;
    private PostResponseDto postResponseDto;
    private UUID sellerId;
    private Seller seller;

    @BeforeEach
    void setUp() {
        seller = SellerFactory.createSeller();
        sellerId = seller.getId();

        post = PostFactory.createPost(sellerId, false);
        postId = post.getId();

        postDto = PostFactory.createPostDto(sellerId, false);
        postResponseDto = PostFactory.createPostResponseDto(postId, sellerId, false);
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

        assertThrows(BadRequest.class, () ->
                postOrchestrator.createPost(postDto));

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

}