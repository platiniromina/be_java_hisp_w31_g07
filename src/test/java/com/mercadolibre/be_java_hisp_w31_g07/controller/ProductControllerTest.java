package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerAveragePriceDto;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
    public static final String GET_POSTS = "/products/post";
    public static final String GET_PROMO_POSTS = "/products/promo-post";
    public static final String GET_PROMO_POST_COUNT = "/products/promo-post/count";
    public static final String GET_PROMO_POST_LIST = "/products/promo-post/list";
    public static final String GET_FOLLOWED_LIST = "/products/followed/{userId}/list";
    public static final String GET_FOLLOWED_SORTED_LIST = "/products/followed/{userId}/sorted";
    public static final String GET_POST_BY_ID = "/products/post/{postId}";
    public static final String GET_USER_AVERAGE_PRICE = "/users/{userId}/average-post-price";

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
    private Post latestPost1;
    private Post latestPost2;

    private UUID sellerId;
    private String userName;

    private Buyer buyer;
    
    private Seller sellerWithPosts;
    private Seller sellerWithNoPosts;

    @BeforeEach
    void setUp() {
        Seller seller = SellerFactory.createSeller(null);
        sellerId = seller.getId();
        sellerWithNoPosts = SellerFactory.createSeller(null);
        sellerWithPosts = SellerFactory.createSeller(null);

        post = PostFactory.createPost(sellerWithPosts.getId(), false);

        latestPost1 = PostFactory.createPost(sellerWithPosts.getId(), false);
        latestPost1.setDate(LocalDate.now().minusDays(2));

        latestPost2 = PostFactory.createPost(sellerWithPosts.getId(), false);
        latestPost2.setDate(LocalDate.now());

        User user = UserFactory.createUser(sellerWithPosts.getId());
        userName = user.getUserName();

        buyer = BuyerFactory.createBuyer(null);

        postRepository.save(post);
        postRepository.save(latestPost1);
        postRepository.save(latestPost2);
        sellerRepository.save(sellerWithPosts);
        userRepository.save(user);
        buyerRepository.save(buyer);
    }


    @Test
    @DisplayName("[SUCCESS] Get sorted posts from followed sellers - ASC")
    void testGetSortedPostsFromFollowedSellersAsc() throws Exception {
        buyer.setFollowed(List.of(sellerWithPosts));
        buyerRepository.save(buyer);

        String expectedResponse = JsonUtil.generateFromDto(Map.of(
                "user_id", buyer.getId(),
                "posts", List.of(latestPost2, latestPost1)
        ));

        ResultActions resultActions = performGetWithParam(buyer.getId(), "date_asc");

        resultActions.andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("[SUCCESS] Get sorted posts from followed sellers - DESC")
    void testGetSortedPostsFromFollowedSellersDesc() throws Exception {
        buyer.setFollowed(List.of(sellerWithPosts));
        buyerRepository.save(buyer);

        String expectedResponse = JsonUtil.generateFromDto(Map.of(
                "user_id", buyer.getId(),
                "posts", List.of(latestPost1, latestPost2)
        ));

        ResultActions resultActions = performGetWithParam(buyer.getId(), "date_desc");

        resultActions.andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("[ERROR] Get sorted posts from followed sellers - Invalid order")
    void testGetSortedPostsFromFollowedSellersInvalidOrder() throws Exception {
        buyer.setFollowed(List.of(sellerWithPosts));
        buyerRepository.save(buyer);
        String invalidOrder = "invalid_order";

        ResultActions resultActions = performGetWithParam(buyer.getId(), invalidOrder);

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorMessagesUtil.invalidSortingParameter("invalid_order")));
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

        ResultActions resultActions = performGetPromoPostList(sellerId);

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

        ResultActions resultActions = performGetPromoPostList(sellerId);
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.noPromotionPostFound(sellerId));
    }

    @Test
    @DisplayName("[ERROR] Get promotion posts by seller - Seller not found")
    void testGetSellerPromoPostsSellerNotFound() throws Exception {
        UUID nonExistentSellerId = UUID.randomUUID();
        ResultActions resultActions = performGetPromoPostList(nonExistentSellerId);
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

        ResultActions resultActions = performGetPromoPostCount(sellerWithPosts.getId());

        resultActions.andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("[ERROR] Get user promo posts count - Seller not found")
    void testGetUserPromoPostsCountSellerNotFound() throws Exception {
        UUID nonExistentSellerId = UUID.randomUUID();
        ResultActions resultActions = performGetPromoPostCount(nonExistentSellerId);
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.sellerNotFound(nonExistentSellerId));
    }

    @Test
    @DisplayName("[SUCCESS] Create post")
    void testCreatePostSuccess() throws Exception {
        PostDto postDto = PostFactory.createPostDto(sellerWithPosts.getId(), false);
        int postRepositorySizeBefore = postRepository.findAll().size();

        ResultActions resultActions = performPost(postDto, GET_POSTS);

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

        ResultActions resultActions = performPost(postDto, GET_POSTS);

        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.sellerNotFound(nonExistentSellerId));
    }

    @Test
    @DisplayName("[SUCCESS] Create promo post")
    void testCreatePromoPostSuccess() throws Exception {
        PostDto postDto = PostFactory.createPostDto(sellerWithPosts.getId(), true);
        int postRepositorySizeBefore = postRepository.findAll().size();

        ResultActions resultActions = performPost(postDto, GET_PROMO_POSTS);

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

        ResultActions resultActions = performPost(postDto, GET_PROMO_POSTS);

        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.sellerNotFound(nonExistentSellerId));
    }

    @Test
    @DisplayName("[SUCCESS] Find post by ID")
    void testFindPostSuccess() throws Exception {
        String expectedResponse = JsonUtil.generateFromDto(PostFactory.createPostResponseDto(sellerWithPosts.getId(), post.getId(), false));
        ResultActions resultActions = performGet(post.getId(), GET_POST_BY_ID);
        resultActions.andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("[ERROR] Find post by ID - Post not found")
    void testFindPostNotFound() throws Exception {
        UUID nonExistentPostId = UUID.randomUUID();
        ResultActions resultActions = performGet(nonExistentPostId, GET_POST_BY_ID);
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.postNotFound(nonExistentPostId));
    }

    @Test
    @DisplayName("[SUCCESS] Get user posts average price")
    void testGetAveragePromoPost() throws Exception {
        Post post1 = PostFactory.createPost(sellerId, false);
        Post post2 = PostFactory.createPost(sellerId, true);

        User user = UserFactory.createUser(sellerId);
        userRepository.save(user);

        sellerRepository.save(SellerFactory.createSeller(sellerId));
        postRepository.save(post1);
        postRepository.save(post2);

        BigDecimal price1 = getEffectivePrice(post1);
        BigDecimal price2 = getEffectivePrice(post2);
        BigDecimal average = price1.add(price2)
                .divide(BigDecimal.valueOf(2), 1, RoundingMode.HALF_UP);

        Double averagePrice = average.doubleValue();

        SellerAveragePriceDto expected = new SellerAveragePriceDto(
                user.getId(),
                user.getUserName(),
                averagePrice
        );

        String expectedResponse = JsonUtil.generateFromDto(expected);

        ResultActions resultActions = performGet(sellerId, GET_USER_AVERAGE_PRICE);

        resultActions.andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("[ERROR] Get user posts average price - User has no posts")
    void testGetAveragePromoPostPostsNotFound() throws Exception {
        UUID nonExistentSellerId = UUID.randomUUID();

        User user = UserFactory.createUser(nonExistentSellerId);
        userRepository.save(user);
        sellerRepository.save(SellerFactory.createSeller(nonExistentSellerId));

        ResultActions resultActions = performGet(user.getId(), GET_USER_AVERAGE_PRICE);
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.userHasNotPosts(user.getId()));
    }

    @Test
    @DisplayName("[ERROR] Get user posts average price - User not found")
    void testGetAveragePromoPostUserNotFound() throws Exception {
        UUID nonExistentPostId = UUID.randomUUID();
        ResultActions resultActions = performGet(nonExistentPostId, GET_USER_AVERAGE_PRICE);
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.sellerNotFound(nonExistentPostId));
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
                "posts", List.of(latestPost1, latestPost2)
        ));

        ResultActions resultActions = performGet(buyer.getId(), GET_FOLLOWED_LIST);
        resultActions.andExpect(status().isOk()).andExpect(content().json(expected));
    }

    @Test
    @DisplayName("[SUCCESS] Get latest posts from from sellers - No posts")
    void testGetLatestPostsFromSellersButNoPostsMatchTheFilter() throws Exception {
        buyer.setFollowed(List.of(sellerWithNoPosts));
        sellerWithNoPosts.setFollowers(List.of(buyer));
        sellerWithNoPosts.incrementFollowerCount();
        ResultActions resultActions = performGet(buyer.getId(), GET_FOLLOWED_LIST);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(buyer.getId().toString()))
                .andExpect(jsonPath("$.posts").isEmpty());
    }

    @Test
    @DisplayName("[ERROR] Get latest posts from from sellers - Buyer not found")
    void testGetLatestPostsFromSellersBuyerNotFound() throws Exception {
        UUID nonExistentBuyerId = UUID.randomUUID();
        ResultActions resultActions = performGet(nonExistentBuyerId, GET_FOLLOWED_LIST);
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.buyerNotFound(nonExistentBuyerId));
    }

    @Test
    @DisplayName("[ERROR] Get latest posts from from sellers - Buyer is not following anyone")
    void testGetLatestPostsFromSellersBuyerNotFollowingAnyone() throws Exception {
        ResultActions resultActions = performGet(buyer.getId(), GET_FOLLOWED_LIST);
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
        return performGetRequest(get(path, id));
    }

    private ResultActions performGetWithParam(UUID id, String paramValue) throws Exception {
        return performGetRequest(get(GET_FOLLOWED_SORTED_LIST, id).param("order", paramValue));
    }

    private ResultActions performGetPromoPostCount(UUID sellerId) throws Exception {
        return performGetRequest(get(GET_PROMO_POST_COUNT, post.getId()).param("user_id", sellerId.toString()));
    }

    private ResultActions performGetPromoPostList(UUID sellerId) throws Exception {
        return performGetRequest(get(GET_PROMO_POST_LIST).param("user_id", sellerId.toString()));
    }

    private ResultActions performPost(PostDto postDto, String path) throws Exception {
        return mockMvc.perform(
                post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.generateFromDto(postDto))
        ).andDo(print());
    }

    private ResultActions performGetRequest(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(
                builder.contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());
    }

    private BigDecimal getEffectivePrice(Post post) {
        BigDecimal price = BigDecimal.valueOf(post.getPrice());
        if (Boolean.TRUE.equals(post.getHasPromo())) {
            BigDecimal discount = BigDecimal.valueOf(post.getDiscount())
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            return price.multiply(BigDecimal.ONE.subtract(discount));
        }
        return price;
    }
}
