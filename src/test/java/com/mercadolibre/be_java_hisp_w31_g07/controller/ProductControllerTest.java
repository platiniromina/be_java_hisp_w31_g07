package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerPromoPostsCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.UserPostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.model.User;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IPostRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IUserRepository;
import com.mercadolibre.be_java_hisp_w31_g07.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {
    @Autowired
    private ISellerRepository sellerRepository;

    @Autowired
    private IPostRepository postRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    private UUID sellerId;
    private UUID postId;
    private String userName;

    @BeforeEach
    void setUp() {
        Seller seller = SellerFactory.createSeller(null);
        sellerId = seller.getId();
        Post post = PostFactory.createPost(sellerId, false);
        postId = post.getId();
        User user = UserFactory.createUser(sellerId);
        userName = user.getUserName();
        postRepository.save(post);
        sellerRepository.save(seller);
        userRepository.save(user);
    }

    @Test
    @DisplayName("[SUCCESS] Get promotion posts by seller")
    void testGetSellerPromoPostsSuccess() throws Exception {
        Post postSaved = PostFactory.createPost(sellerId, true);

        userRepository.save(UserFactory.createUser(sellerId));
        sellerRepository.save(SellerFactory.createSeller(sellerId));
        postRepository.save(postSaved);

        PostResponseDto postResponseDto = PostFactory.createPostResponseDto(
                postSaved.getSellerId(), postSaved.getId(), true
        );

        UserPostResponseDto expectedDto = UserFactory.createUserPostResponseDto(sellerId);
        expectedDto.setPostList(List.of(postResponseDto));

        String expectedResponse = JsonUtil.generateFromDto(expectedDto);

        ResultActions resultActions = performGetPromoPostList(sellerId, "/products/promo-post/list");

        resultActions.andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("[ERROR] Get promotion posts by seller - Seller has not posts with promo")
    void testGetSellerPromoPostsPostNotFound() throws Exception {
        Post postSaved = PostFactory.createPost(sellerId, false);

        userRepository.save(UserFactory.createUser(sellerId));
        sellerRepository.save(SellerFactory.createSeller(sellerId));
        postRepository.save(postSaved);

        ResultActions resultActions = performGetPromoPostList(sellerId, "/products/promo-post/list");
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.noPromotionPostFound(sellerId));
    }

    @Test
    @DisplayName("[ERROR] Get promotion posts by seller - Seller not found")
    void testGetSellerPromoPostsSellerNotFound() throws Exception {
        UUID nonExistentSellerId = UUID.randomUUID();
        ResultActions resultActions = performGetPromoPostList(nonExistentSellerId, "/products/promo-post/list");
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.sellerNotFound(nonExistentSellerId));
    }


    @Test
    @DisplayName("[SUCCESS] Get user promo posts count")
    void testGetUserPromoPostsCountSuccess() throws Exception {
        Post promoPost = PostFactory.createPost(sellerId, true);
        postRepository.save(promoPost);
        String expectedResponse = JsonUtil.generateFromDto(
                new SellerPromoPostsCountResponseDto(sellerId, userName, 1)
        );

        ResultActions resultActions = performGetPromoPostCount(sellerId, "/products/promo-post/count");

        resultActions.andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("[ERROR] Get user promo posts count - Seller not found")
    void testGetUserPromoPostsCountSellerNotFound() throws Exception {
        UUID nonExistentSellerId = UUID.randomUUID();
        ResultActions resultActions = performGetPromoPostCount(nonExistentSellerId, "/products/promo-post/count");
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.sellerNotFound(nonExistentSellerId));
    }

    @Test
    @DisplayName("[SUCCESS] Create post")
    void testCreatePostSuccess() throws Exception {
        PostDto postDto = PostFactory.createPostDto(sellerId, false);
        int postRepositorySizeBefore = postRepository.findAll().size();

        ResultActions resultActions = performPost(postDto, "/products/post");

        PostResponseDto response = JsonUtil
                .fromJsonToDto(resultActions
                        .andReturn().getResponse().getContentAsString(), PostResponseDto.class);
        resultActions.andExpect(status().isOk());
        assertCreatedPost(response, postDto, postRepositorySizeBefore);

    }

    @Test
    @DisplayName("[ERROR] Create post - Seller not found")
    void testCreatePostError() throws Exception {
        UUID nonExistentSellerId = UUID.randomUUID();
        PostDto postDto = PostFactory.createPostDto(nonExistentSellerId, false);

        ResultActions resultActions = performPost(postDto, "/products/post");

        assertBadRequestWithMessage(resultActions, "Seller " + nonExistentSellerId + " not found");
    }

    @Test
    @DisplayName("[SUCCESS] Create promo post")
    void testCreatePromoPostSuccess() throws Exception {
        PostDto postDto = PostFactory.createPostDto(sellerId, true);
        int postRepositorySizeBefore = postRepository.findAll().size();

        ResultActions resultActions = performPost(postDto, "/products/promo-post");

        PostResponseDto response = JsonUtil
                .fromJsonToDto(resultActions
                        .andReturn().getResponse().getContentAsString(), PostResponseDto.class);
        resultActions.andExpect(status().isOk());
        assertCreatedPost(response, postDto, postRepositorySizeBefore);

    }

    @Test
    @DisplayName("[ERROR] Create promo post - Seller not found")
    void testCreatePromoPostError() throws Exception {
        UUID nonExistentSellerId = UUID.randomUUID();
        PostDto postDto = PostFactory.createPostDto(nonExistentSellerId, true);

        ResultActions resultActions = performPost(postDto, "/products/promo-post");

        assertBadRequestWithMessage(resultActions, "Seller " + nonExistentSellerId + " not found");
    }

    @Test
    @DisplayName("[SUCCESS] Find post by ID")
    void testFindPostSuccess() throws Exception {
        String expectedResponse = JsonUtil.generateFromDto(PostFactory.createPostResponseDto(sellerId, postId, false));
        ResultActions resultActions = performGet(postId, "/products/post/{postId}");
        resultActions.andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("[ERROR] Find post by ID - Post not found")
    void testFindPostNotFound() throws Exception {
        UUID nonExistentPostId = UUID.randomUUID();
        ResultActions resultActions = performGet(nonExistentPostId, "/products/post/{postId}");
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.postNotFound(nonExistentPostId));
    }


    private void assertBadRequestWithMessage(ResultActions resultActions, String expectedMessage) throws Exception {
        resultActions.andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertEquals(expectedMessage,
                                Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    private void assertCreatedPost(PostResponseDto response, PostDto postDto, int postRepositorySizeBefore) {
        assertAll(
                () -> assertNotNull(response.getId()),
                () -> assertEquals(response.getDate(), postDto.getDate()),
                () -> assertEquals(response.getProduct().getProductName(), postDto.getProduct().getProductName()),
                () -> assertEquals(response.getCategory(), postDto.getCategory()),
                () -> assertEquals(response.getPrice(), postDto.getPrice()),
                () -> assertEquals(response.getDiscount(), postDto.getDiscount()),
                () -> assertEquals(response.getSellerId(), postDto.getSellerId()),
                () -> assertEquals(postRepository.findAll().size(), postRepositorySizeBefore + 1)
        );
    }

    private ResultActions performGet(UUID postId, String path) throws Exception {
        return mockMvc.perform(
                get(path, postId)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());
    }

    private ResultActions performGetPromoPostCount(UUID sellerId, String path) throws Exception {
        return mockMvc.perform(
                get(path, postId)
                        .param("user_id", sellerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());
    }

    private ResultActions performPost(PostDto postDto, String path) throws Exception {
        return mockMvc.perform(
                post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.generateFromDto(postDto))
        ).andDo(print());
    }

    private ResultActions performGetPromoPostList(UUID sellerId, String path) throws Exception {
        return mockMvc.perform(
                get(path)
                        .param("user_id", sellerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());
    }

}
