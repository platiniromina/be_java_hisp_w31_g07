package com.mercadolibre.be_java_hisp_w31_g07.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private Buyer buyer;
    private Seller sellerWithFollowers;
    private User userWithFollowers;
    private Seller sellerWithBuyerFollower;
    private Buyer buyerWithFollowedSeller;

    @BeforeEach
    void setUp() {
        seller = SellerFactory.createSeller(null);
        buyer = BuyerFactory.createBuyer(null);

        sellerWithBuyerFollower = SellerFactory.createSeller(null);
        buyerWithFollowedSeller = BuyerFactory.createBuyer(null);

        sellerWithBuyerFollower.addFollower(buyerWithFollowedSeller);
        buyerWithFollowedSeller.addFollowedSeller(sellerWithBuyerFollower);

        sellerWithFollowers = SellerFactory.createSellerWithFollowers(3);
        userWithFollowers = UserFactory.createUser(sellerWithFollowers.getId());

        sellerRepository.save(sellerWithFollowers);
        userRepository.save(userWithFollowers);

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

    private ResultActions performFollow(UUID buyerId, UUID sellerId) throws Exception {
        return mockMvc.perform(
                post("/users/{buyerId}/follow/{sellerId}", buyerId, sellerId)
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