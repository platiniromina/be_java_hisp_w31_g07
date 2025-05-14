package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.BuyerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.model.User;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IBuyerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IUserRepository;
import com.mercadolibre.be_java_hisp_w31_g07.util.*;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
class FollowControllerTest {
    public static final String GET_SELLER_FOLLOWERS = "/{sellerId}/followers";
    public static final String GET_USER_FOLLOWERS_COUNT = "/users/{userId}/followers/count";
    public static final String GET_USER_FOLLOWERS_LIST = "/users/{userId}/followers/list";
    public static final String GET_USER_FOLLOWED_LIST = "/users/{userId}/followed/list";

    @Autowired
    private ISellerRepository sellerRepository;

    @Autowired
    private IBuyerRepository buyerRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    private MockMvcUtil mockMvcUtil;

    private Seller seller;
    private Seller sellerWithBuyerFollower;
    private Seller sellerWithFollowers;

    private Buyer buyer;
    private Buyer buyerA;
    private Buyer buyerB;

    private Buyer buyerWithFollowedSeller;
    private User userSeller;
    private User userSellerWithBuyerFollower;
    private User userWithFollowers;

    private User userBuyerA;
    private User userBuyerB;

    private SellerResponseDto sellerWithBuyerFollowerDto;
    private BuyerResponseDto buyerWithFollowedSellerList;

    @BeforeEach
    void setUp() {
        mockMvcUtil = new MockMvcUtil(mockMvc);

        seller = SellerFactory.createSeller(null);
        userSeller = UserFactory.createUser(seller.getId());

        buyer = BuyerFactory.createBuyer(null);

        userBuyerA = UserFactory.createUser(null);
        buyerA = BuyerFactory.createBuyer(userBuyerA.getId());

        userBuyerB = UserFactory.createUser(null);
        buyerB = BuyerFactory.createBuyer(userBuyerB.getId());

        sellerWithBuyerFollower = SellerFactory.createSeller(null);
        buyerWithFollowedSeller = BuyerFactory.createBuyer(null);
        BuyerResponseDto buyerWithFollowedSellerDto = BuyerFactory.createBuyerResponseDto(buyerWithFollowedSeller.getId());
        sellerWithBuyerFollowerDto = SellerFactory.createSellerResponseDtoFollowers(sellerWithBuyerFollower.getId(), buyerWithFollowedSellerDto);
        buyerWithFollowedSellerList = BuyerFactory.createBuyerResponseDtoFollowed(buyerWithFollowedSeller.getId(), sellerWithBuyerFollowerDto);

        sellerWithBuyerFollower.addFollower(buyerWithFollowedSeller);
        buyerWithFollowedSeller.addFollowedSeller(sellerWithBuyerFollower);

        userSellerWithBuyerFollower = UserFactory.createUser(sellerWithBuyerFollower.getId());
        User userBuyerWithFollowedSeller = UserFactory.createUser(buyerWithFollowedSeller.getId());

        sellerWithFollowers = SellerFactory.createSellerWithFollowers(3);
        userWithFollowers = UserFactory.createUser(sellerWithFollowers.getId());

        userRepository.save(userSeller);
        userRepository.save(userSellerWithBuyerFollower);
        userRepository.save(userBuyerWithFollowedSeller);
        userRepository.save(userBuyerA);
        userRepository.save(userBuyerB);
        userRepository.save(userWithFollowers);

        sellerRepository.save(seller);
        sellerRepository.save(sellerWithBuyerFollower);
        sellerRepository.save(sellerWithFollowers);

        buyerRepository.save(buyer);
        buyerRepository.save(buyerWithFollowedSeller);
    }

    @Test
    @DisplayName("[SUCCESS] Get sorted followers in ascending order")
    void testGetSortedFollowersAsc() throws Exception {
        userSeller.setUserName("C");
        userBuyerA.setUserName("A");
        userBuyerB.setUserName("B");

        userRepository.save(userSeller);
        userRepository.save(userBuyerA);
        userRepository.save(userBuyerB);

        seller.addFollower(buyerA);
        seller.addFollower(buyerB);

        String order = "name_asc";

        ResultActions result = mockMvcUtil.performGet(GET_SELLER_FOLLOWERS,
                Map.of("sellerId", seller.getId().toString()),
                Map.of("order", order));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.followers[0].user_name").value(userBuyerA.getUserName()))
                .andExpect(jsonPath("$.followers[1].user_name").value(userBuyerB.getUserName()));
    }

    @Test
    @DisplayName("[SUCCESS] Get sorted followers in descending order")
    void testGetSortedFollowersDesc() throws Exception {
        userSeller.setUserName("C");
        userBuyerA.setUserName("A");
        userBuyerB.setUserName("B");

        userRepository.save(userSeller);
        userRepository.save(userBuyerA);
        userRepository.save(userBuyerB);

        seller.addFollower(buyerA);
        seller.addFollower(buyerB);

        String order = "name_desc";

        ResultActions result = mockMvcUtil.performGet(GET_SELLER_FOLLOWERS,
                Map.of("sellerId", seller.getId().toString()),
                Map.of("order", order));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.followers[0].user_name").value(userBuyerB.getUserName()))
                .andExpect(jsonPath("$.followers[1].user_name").value(userBuyerA.getUserName()));
    }

    @Test
    @DisplayName("[ERROR] Get sorted followers with invalid order")
    void testGetSortedFollowersInvalidOrder() throws Exception {
        String invalidOrder = "invalid_order";

        ResultActions result = mockMvcUtil.performGet(GET_SELLER_FOLLOWERS,
                Map.of("sellerId", seller.getId().toString()),
                Map.of("order", invalidOrder));

        assertBadRequestWithMessage(result, ErrorMessagesUtil.invalidSortingParameter(invalidOrder));
    }


    @Test
    @DisplayName("[SUCCESS] Follow a seller")
    void testFollowSellerSuccess() throws Exception {
        ResultActions resultActions = performFollow(buyer.getId(), seller.getId());
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("[ERROR] Follow a seller with invalid buyerId")
    void testFollowSellerSameUserError() throws Exception {
        UUID invalidBuyerId = UUID.randomUUID();
        ResultActions resultActions = performFollow(invalidBuyerId, seller.getId());
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.buyerNotFound(invalidBuyerId));
    }

    @Test
    @DisplayName("[ERROR] Follow a seller with invalid sellerId")
    void testFollowSellerAlreadyFollowingError() throws Exception {
        UUID buyerId = buyerWithFollowedSeller.getId();
        UUID sellerId = sellerWithBuyerFollower.getId();
        ResultActions resultActions = performFollow(buyerId, sellerId);
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.buyerAlreadyFollowingSeller(buyerId, sellerId));
    }

    @Test
    @DisplayName("[ERROR] Follow a seller that does not exist")
    void testFollowSellerThatDoesNotExistError() throws Exception {
        UUID sellerId = UUID.randomUUID();
        ResultActions resultActions = performFollow(buyer.getId(), sellerId);
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.sellerNotFound(sellerId));
    }

    @Test
    @DisplayName("[ERROR] Follow a seller with invalid buyerId")
    void testFollowSellerButBuyerNotFoundError() throws Exception {
        UUID buyerId = UUID.randomUUID();
        ResultActions resultActions = performFollow(buyerId, seller.getId());
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.buyerNotFound(buyerId));
    }

    @Test
    @DisplayName("[SUCCESS] Get followers count")
    void testGetFollowersCountSuccess() throws Exception {
        UUID sellerId = sellerWithFollowers.getId();

        ResultActions resultActions = mockMvcUtil.performGet(GET_USER_FOLLOWERS_COUNT,
                Map.of("userId", sellerId.toString()));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.followers_count").value(sellerWithFollowers.getFollowerCount()))
                .andExpect(jsonPath("$.user_id").value(sellerId.toString()))
                .andExpect(jsonPath("$.user_name").value(userWithFollowers.getUserName()));

    }

    @Test
    @DisplayName("[ERROR] Get followers count - Seller not found")
    void testGetFollowersCountUserNotFound() throws Exception {
        UUID userId = UUID.randomUUID();

        ResultActions resultActions = mockMvcUtil.performGet(GET_USER_FOLLOWERS_COUNT,
                Map.of("userId", userId.toString()));

        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.sellerNotFound(userId));
    }

    @Test
    @DisplayName("[SUCCESS] Unfollow seller")
    void testUnfollowSellerSuccess() throws Exception {
        ResultActions resultActions = performUnfollow(buyerWithFollowedSeller.getId(), sellerWithBuyerFollower.getId());
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("[ERROR] Unfollow seller - buyer is not following seller")
    void testUnfollowSellerWhoImNotFollowingError() throws Exception {
        ResultActions resultActions = performUnfollow(buyer.getId(), seller.getId());
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.buyerNotFollowingSeller(buyer.getId(), seller.getId()));
    }

    @Test
    @DisplayName("[ERROR] Unfollow seller - seller not found")
    void testUnfollowSellerThatDoesNotExistError() throws Exception {
        UUID sellerId = UUID.randomUUID();
        ResultActions resultActions = performUnfollow(buyer.getId(), sellerId);
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.sellerNotFound(sellerId));
    }

    @Test
    @DisplayName("[ERROR] Unfollow seller - buyer not found")
    void testUnfollowSellerButBuyerNotFoundError() throws Exception {
        UUID buyerId = UUID.randomUUID();
        ResultActions resultActions = performUnfollow(buyerId, seller.getId());
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.buyerNotFound(buyerId));
    }

    @Test
    @DisplayName("[SUCCESS] Get Followers List")
    void testFindFollowers() throws Exception {
        UUID sellerId = userSellerWithBuyerFollower.getId();

        ResultActions resultActions = mockMvcUtil.performGet(GET_USER_FOLLOWERS_LIST,
                Map.of("userId", sellerId.toString()));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.followers[0].user_id").value(sellerWithBuyerFollower.getFollowers().get(0).getId().toString()))
                .andExpect(jsonPath("$.followers[0].user_name").value(sellerWithBuyerFollowerDto.getFollowers().get(0).getUserName()))
                .andExpect(jsonPath("$.user_id").value(sellerId.toString()))
                .andExpect(jsonPath("$.user_name").value(userWithFollowers.getUserName()));
    }

    @Test
    @DisplayName("[ERROR] Get Followers List - Seller not found")
    void testFindFollowersSellerNotFound() throws Exception {
        UUID sellerId = UUID.randomUUID();

        ResultActions resultActions = mockMvcUtil.performGet(GET_USER_FOLLOWERS_LIST,
                Map.of("userId", sellerId.toString()));

        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.sellerNotFound(sellerId));
    }

    @Test
    @DisplayName("[ERROR] Get Followers List - User not found")
    void testFindFollowersUserNotFound() throws Exception {
        Seller sellerRandom = SellerFactory.createSeller(UUID.randomUUID());
        sellerRepository.save(sellerRandom);

        ResultActions resultActions = mockMvcUtil.performGet(GET_USER_FOLLOWERS_LIST,
                Map.of("userId", sellerRandom.getId().toString()));

        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.userNotFound(sellerRandom.getId()));
    }

    @Test
    @DisplayName("[SUCCESS] Get Followers List")
    void testFindFollowed() throws Exception {
        UUID buyerId = buyerWithFollowedSeller.getId();

        ResultActions resultActions = mockMvcUtil.performGet(GET_USER_FOLLOWED_LIST,
                Map.of("userId", buyerId.toString()));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.followed[0].user_id").value(buyerWithFollowedSeller.getFollowed().get(0).getId().toString()))
                .andExpect(jsonPath("$.followed[0].user_name").value(buyerWithFollowedSellerList.getFollowed().get(0).getUserName()))
                .andExpect(jsonPath("$.user_id").value(buyerId.toString()))
                .andExpect(jsonPath("$.user_name").value(userWithFollowers.getUserName()));
    }

    @Test
    @DisplayName("[ERROR] Get Followers List - Buyer not found")
    void testFindFollowedBuyerNotFound() throws Exception {
        UUID buyerId = UUID.randomUUID();

        ResultActions resultActions = mockMvcUtil.performGet(GET_USER_FOLLOWED_LIST,
                Map.of("userId", buyerId.toString()));

        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.buyerNotFound(buyerId));
    }

    @Test
    @DisplayName("[ERROR] Get Followed List - User not found")
    void testFindFollowedsUserNotFound() throws Exception {
        Buyer buyerRandom = BuyerFactory.createBuyer(UUID.randomUUID());
        buyerRepository.save(buyerRandom);

        ResultActions resultActions = mockMvcUtil.performGet(GET_USER_FOLLOWED_LIST,
                Map.of("userId", buyerRandom.getId().toString()));

        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.userNotFound(buyerRandom.getId()));
    }

    private ResultActions performFollow(UUID buyerId, UUID sellerId) throws Exception {
        Map<String, String> pathVars = new HashMap<>();
        pathVars.put("buyerId", buyerId.toString());
        pathVars.put("sellerId", sellerId.toString());

        return mockMvcUtil.performPostRequest("/users/{buyerId}/follow/{sellerId}", pathVars, null);
    }

    private ResultActions performUnfollow(UUID buyerId, UUID sellerId) throws Exception {
        Map<String, String> pathVars = new HashMap<>();
        pathVars.put("userId", buyerId.toString());
        pathVars.put("userIdToUnfollow", sellerId.toString());

        return mockMvcUtil.performPutRequest("/users/{userId}/unfollow/{userIdToUnfollow}", pathVars, null);
    }

    private void assertBadRequestWithMessage(ResultActions resultActions, String expectedMessage) throws Exception {
        resultActions.andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertEquals(expectedMessage,
                                Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
}