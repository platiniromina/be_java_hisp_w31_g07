package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IPostRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.util.ErrorMessagesUtil;
import com.mercadolibre.be_java_hisp_w31_g07.util.JsonUtil;
import com.mercadolibre.be_java_hisp_w31_g07.util.PostFactory;
import com.mercadolibre.be_java_hisp_w31_g07.util.SellerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
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
    private MockMvc mockMvc;

    private UUID sellerId;
    private UUID postId;

    @BeforeEach
    void setUp() {
        Seller seller = SellerFactory.createSeller();
        sellerId = seller.getId();
        Post post = PostFactory.createPost(sellerId, false, LocalDateTime.now());
        postId = post.getId();
        postRepository.save(post);
        sellerRepository.save(seller);
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

}
