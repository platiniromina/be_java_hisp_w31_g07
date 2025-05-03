package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerPromoPostsCountResponseDto;
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

import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        Seller seller = SellerFactory.createSeller();
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

}
