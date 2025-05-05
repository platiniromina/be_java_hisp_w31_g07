package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.BuyerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.model.User;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IBuyerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IUserRepository;
import com.mercadolibre.be_java_hisp_w31_g07.util.BuyerFactory;
import com.mercadolibre.be_java_hisp_w31_g07.util.ErrorMessagesUtil;
import com.mercadolibre.be_java_hisp_w31_g07.util.SellerFactory;
import com.mercadolibre.be_java_hisp_w31_g07.util.UserFactory;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FollowControllerTest {
    @Autowired
    private ISellerRepository sellerRepository;

    @Autowired
    private IBuyerRepository buyerRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    private Seller seller;
    private SellerResponseDto sellerWithBuyerFollowerDto;
    private Buyer buyer;
    private BuyerResponseDto followerDto;
    private Seller sellerWithFollowers;
    private User userWithFollowers;
    private Seller sellerWithBuyerFollower;
    private Buyer buyerWithFollowedSeller;
    private User userSellerWithBuyerFollower;
    private User userBuyerWithFollowedSeller;

    @BeforeEach
    void setUp() {
        seller = SellerFactory.createSeller(null);
        buyer = BuyerFactory.createBuyer(null);

        sellerWithBuyerFollower = SellerFactory.createSeller(null);
        buyerWithFollowedSeller = BuyerFactory.createBuyer(null);
        BuyerResponseDto buyerWithFollowedSellerDto = BuyerFactory.createBuyerResponseDto(buyerWithFollowedSeller.getId());
        sellerWithBuyerFollowerDto = SellerFactory.createSellerResponseDtoFollowers(sellerWithBuyerFollower.getId(), buyerWithFollowedSellerDto);

        sellerWithBuyerFollower.addFollower(buyerWithFollowedSeller);
        buyerWithFollowedSeller.addFollowedSeller(sellerWithBuyerFollower);

        userSellerWithBuyerFollower = UserFactory.createUser(sellerWithBuyerFollower.getId());
        userBuyerWithFollowedSeller = UserFactory.createUser(buyerWithFollowedSeller.getId());

        sellerWithFollowers = SellerFactory.createSellerWithFollowers(3);
        userWithFollowers = UserFactory.createUser(sellerWithFollowers.getId());

        sellerRepository.save(sellerWithFollowers);
        userRepository.save(userWithFollowers);
        userRepository.save(userSellerWithBuyerFollower);
        userRepository.save(userBuyerWithFollowedSeller);

        sellerRepository.save(seller);
        buyerRepository.save(buyer);

        sellerRepository.save(sellerWithBuyerFollower);
        buyerRepository.save(buyerWithFollowedSeller);
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

        ResultActions resultActions = performGet(sellerId, "/users/{userId}/followers/count");

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.followers_count").value(sellerWithFollowers.getFollowerCount()))
                .andExpect(jsonPath("$.user_id").value(sellerId.toString()))
                .andExpect(jsonPath("$.user_name").value(userWithFollowers.getUserName()));

    }

    @Test
    @DisplayName("[ERROR] Get followers count - Seller not found")
    void testGetFollowersCountUserNotFound() throws Exception {
        UUID userId = UUID.randomUUID();

        ResultActions resultActions = performGet(userId, "/users/{userId}/followers/count");

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorMessagesUtil.sellerNotFound(userId)));
    }

    private ResultActions performGet(UUID userId, String path) throws Exception {
        return mockMvc.perform(
                get(path, userId)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());
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

        ResultActions resultActions = performGet(sellerId, "/users/{userId}/followers/list");

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.followers[0].user_id").value(sellerWithBuyerFollower.getFollowers().get(0).getId().toString()))
                .andExpect(jsonPath("$.followers[0].user_name").value(sellerWithBuyerFollowerDto.getFollowers().get(0).getUserName()))
                .andExpect(jsonPath("$.user_id").value(sellerId.toString()))
                .andExpect(jsonPath("$.user_name").value(userWithFollowers.getUserName()));
    }

    private ResultActions performFollow(UUID buyerId, UUID sellerId) throws Exception {
        return mockMvc.perform(
                post("/users/{buyerId}/follow/{sellerId}", buyerId, sellerId)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());
    }

    private ResultActions performUnfollow(UUID buyerId, UUID sellerId) throws Exception {
        return mockMvc.perform(
                put("/users/{userId}/unfollow/{userIdToUnfollow}", buyerId, sellerId)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());
    }

    private void assertBadRequestWithMessage(ResultActions resultActions, String expectedMessage) throws Exception {
        resultActions.andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertEquals(expectedMessage,
                                Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
}