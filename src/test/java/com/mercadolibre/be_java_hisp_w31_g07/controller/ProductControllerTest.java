package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerPromoPostsCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.UserPostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.model.User;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IPostRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IUserRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.implementations.BuyerRepository;
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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Autowired
    private BuyerRepository buyerRepository;

    private Post post;
    private String userName;
    private Buyer buyer;
    private Seller sellerWithPosts;
    private Seller sellerWithNoPosts;

    @BeforeEach
    void setUp() {
        sellerWithNoPosts = SellerFactory.createSeller(null);
        sellerWithPosts = SellerFactory.createSeller(null);
        post = PostFactory.createPost(sellerWithPosts.getId(), false);
        User user = UserFactory.createUser(sellerWithPosts.getId());
        userName = user.getUserName();
        buyer = BuyerFactory.createBuyer(null);

        postRepository.save(post);
        sellerRepository.save(sellerWithPosts);
        userRepository.save(user);
        buyerRepository.save(buyer);
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
        Post promoPost = PostFactory.createPost(sellerWithPosts.getId(), true);
        postRepository.save(promoPost);
        String expectedResponse = JsonUtil.generateFromDto(
                new SellerPromoPostsCountResponseDto(sellerWithPosts.getId(), userName, 1)
        );

        ResultActions resultActions = performGetPromoPostCount(sellerWithPosts.getId(), "/products/promo-post/count");

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
        PostDto postDto = PostFactory.createPostDto(sellerWithPosts.getId(), false);
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
        PostDto postDto = PostFactory.createPostDto(sellerWithPosts.getId(), true);
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
        String expectedResponse = JsonUtil.generateFromDto(PostFactory.createPostResponseDto(sellerWithPosts.getId(), post.getId(), false));
        ResultActions resultActions = performGet(post.getId(), "/products/post/{postId}");
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

    @Test
    @DisplayName("[SUCCESS] Get latest posts from from sellers")
    void testGetLatestPostsFromSellers() throws Exception {
        buyer.setFollowed(List.of(sellerWithPosts));
        sellerWithPosts.setFollowers(List.of(buyer));
        sellerWithPosts.incrementFollowerCount();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String expected = mapper.writeValueAsString(Map.of(
                "user_id", buyer.getId(),
                "posts", List.of(post)
        ));

        ResultActions resultActions = performGet(buyer.getId(), "/products/followed/{userId}/list");
        resultActions.andExpect(status().isOk()).andExpect(content().json(expected));
    }

    @Test
    @DisplayName("[SUCCESS] Get latest posts from from sellers - No posts")
    void testGetLatestPostsFromSellersButNoPostsMatchTheFilter() throws Exception {
        buyer.setFollowed(List.of(sellerWithNoPosts));
        sellerWithNoPosts.setFollowers(List.of(buyer));
        sellerWithNoPosts.incrementFollowerCount();
        ResultActions resultActions = performGet(buyer.getId(), "/products/followed/{userId}/list");
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(buyer.getId().toString()))
                .andExpect(jsonPath("$.posts").isEmpty());
    }

    @Test
    @DisplayName("[ERROR] Get latest posts from from sellers - Buyer not found")
    void testGetLatestPostsFromSellersBuyerNotFound() throws Exception {
        UUID nonExistentBuyerId = UUID.randomUUID();
        ResultActions resultActions = performGet(nonExistentBuyerId, "/products/followed/{userId}/list");
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.buyerNotFound(nonExistentBuyerId));
    }

    @Test
    @DisplayName("[ERROR] Get latest posts from from sellers - Buyer is not following anyone")
    void testGetLatestPostsFromSellersBuyerNotFollowingAnyone() throws Exception {
        ResultActions resultActions = performGet(buyer.getId(), "/products/followed/{userId}/list");
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.buyerIsNotFollowingAnySellers(buyer.getId()));
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

    private ResultActions performGet(UUID id, String path) throws Exception {
        return mockMvc.perform(
                get(path, id)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());
    }

    private ResultActions performGetPromoPostCount(UUID sellerId, String path) throws Exception {
        return mockMvc.perform(
                get(path, post.getId())
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
