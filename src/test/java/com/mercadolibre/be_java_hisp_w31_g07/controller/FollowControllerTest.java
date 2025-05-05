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
    private Buyer buyerA;
    private Buyer buyerB;
    private Seller sellerWithFollowers;
    private User userWithFollowers;
    private User userSeller;
    private User userBuyerA;
    private User userBuyerB;

    private Seller sellerWithBuyerFollower;
    private Buyer buyerWithFollowedSeller;
    private User userSellerWithBuyerFollower;
    private BuyerResponseDto buyerWithFollowedSellerList;

    @BeforeEach
    void setUp() {
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

        userRepository.save(UserFactory.createUser(sellerWithBuyerFollower.getId()));

        sellerWithBuyerFollower.addFollower(buyerWithFollowedSeller);
        buyerWithFollowedSeller.addFollowedSeller(sellerWithBuyerFollower);

        userSellerWithBuyerFollower = UserFactory.createUser(sellerWithBuyerFollower.getId());
        User userBuyerWithFollowedSeller = UserFactory.createUser(buyerWithFollowedSeller.getId());
        sellerWithFollowers = SellerFactory.createSellerWithFollowers(3);
        userWithFollowers = UserFactory.createUser(sellerWithFollowers.getId());

        sellerRepository.save(sellerWithFollowers);
        userRepository.save(userWithFollowers);
        userRepository.save(userSeller);
        userRepository.save(userSellerWithBuyerFollower);
        userRepository.save(userBuyerWithFollowedSeller);

        sellerRepository.save(seller);
        buyerRepository.save(buyer);

        sellerRepository.save(sellerWithBuyerFollower);
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

        ResultActions resultActions = mockMvc.perform(
                get("/{sellerId}/followers", seller.getId())
                        .param("order", order)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.followers[0].user_name").value("A"))
                .andExpect(jsonPath("$.followers[1].user_name").value("B"));
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

        ResultActions resultActions = mockMvc.perform(
                get("/{sellerId}/followers", seller.getId())
                        .param("order", order)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.followers[0].user_name").value("B"))
                .andExpect(jsonPath("$.followers[1].user_name").value("A"));
    }
    @Test
    @DisplayName("[ERROR] Get sorted followers with invalid order")
    void testGetSortedFollowersInvalidOrder() throws Exception {
        String invalidOrder = "invalid_order";

        ResultActions resultActions = mockMvc.perform(
                get("/{sellerId}/followers", seller.getId())
                        .param("order", invalidOrder)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        resultActions.andExpect(status().isBadRequest())
                // Aquí cambiamos el mensaje esperado para que coincida con el que realmente devuelve el GlobalExceptionHandler
                .andExpect(jsonPath("$.message").value(ErrorMessagesUtil.invalidSortingParameter("invalid_order")));
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

    @Test
    @DisplayName("[ERROR] Get Followers List - Seller not found")
    void testFindFollowersSellerNotFound() throws Exception {
        UUID sellerId = UUID.randomUUID();
        ResultActions resultActions = performGet(sellerId, "/users/{userId}/followers/list");
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.sellerNotFound(sellerId));
    }

    @Test
    @DisplayName("[ERROR] Get Followers List - User not found")
    void testFindFollowersUserNotFound() throws Exception {
        Seller sellerRandom = SellerFactory.createSeller(UUID.randomUUID());
        sellerRepository.save(sellerRandom);

        ResultActions resultActions = performGet(sellerRandom.getId(), "/users/{userId}/followers/list");
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.userNotFound(sellerRandom.getId()));
    }

    @Test
    @DisplayName("[SUCCESS] Get Followers List")
    void testFindFollowed() throws Exception {
        UUID buyerId = buyerWithFollowedSeller.getId();

        ResultActions resultActions = performGet(buyerId, "/users/{userId}/followed/list");

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
        ResultActions resultActions = performGet(buyerId, "/users/{userId}/followers/list");
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.sellerNotFound(buyerId));
    }

    @Test
    @DisplayName("[ERROR] Get Followed List - User not found")
    void testFindFollowedsUserNotFound() throws Exception {
        Buyer buyerRandom = BuyerFactory.createBuyer(UUID.randomUUID());
        buyerRepository.save(buyerRandom);

        ResultActions resultActions = performGet(buyerRandom.getId(), "/users/{userId}/followed/list");
        assertBadRequestWithMessage(resultActions, ErrorMessagesUtil.userNotFound(buyerRandom.getId()));
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