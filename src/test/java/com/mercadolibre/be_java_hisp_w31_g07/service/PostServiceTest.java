package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IPostRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.implementations.PostService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private IPostRepository postRepository;

    @Mock
    private ISellerService sellerService;

    @Mock
    private IProductService productService;

    @Mock
    private GenericObjectMapper mapper;

    @InjectMocks
    private PostService postService;

    private PostDto postDto;
    private Post post;
    private PostResponseDto postResponseDto;
    private Seller seller;

    @BeforeEach
    void setUp() {

        seller = SellerFactory.createSeller();
        postDto = PostFactory.createPostDto(seller.getId());
        post = PostFactory.createPost(seller.getId());
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

}
