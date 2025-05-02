package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.implementations.BuyerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.implementations.BuyerService;
import com.mercadolibre.be_java_hisp_w31_g07.util.BuyerFactory;
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

import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SellerControllerTest {
    @Autowired
    private ISellerRepository sellerRepository;

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private MockMvc mockMvc;

    private Seller seller;
    private Buyer buyer;
    private Seller sellerWithBuyerFollower;
    private Buyer buyerWithFollowedSeller;
    @Autowired
    private BuyerService buyerService;

    @BeforeEach
    void setUp() {
        seller = SellerFactory.createSeller();
        buyer = BuyerFactory.createBuyer();

        sellerWithBuyerFollower = SellerFactory.createSeller();
        buyerWithFollowedSeller = BuyerFactory.createBuyer();

        sellerWithBuyerFollower.addFollower(buyerWithFollowedSeller);
        buyerWithFollowedSeller.addFollowedSeller(sellerWithBuyerFollower);

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
        ResultActions resultActions = performFollow(buyer.getId(), buyer.getId());
        assertBadRequestWithMessage(resultActions, "User cannot follow themselves.");
    }

    @Test
    @DisplayName("[ERROR] Follow a seller with invalid sellerId")
    void testFollowSellerAlreadyFollowingError() throws Exception {
        UUID buyerId = buyerWithFollowedSeller.getId();
        UUID sellerId = sellerWithBuyerFollower.getId();
        ResultActions resultActions = performFollow(buyerId, sellerId);
        assertBadRequestWithMessage(resultActions, "Buyer " + buyerId + " already follows seller " + sellerId);
    }

    @Test
    @DisplayName("[ERROR] Follow a seller that does not exist")
    void testFollowSellerThatDoesNotExistError() throws Exception {
        UUID sellerId = UUID.randomUUID();
        ResultActions resultActions = performFollow(buyer.getId(), sellerId);
        assertBadRequestWithMessage(resultActions, "Seller " + sellerId + " not found");
    }

    @Test
    @DisplayName("[ERROR] Follow a seller with invalid buyerId")
    void testFollowSellerButBuyerNotFoundError() throws Exception {
        UUID buyerId = UUID.randomUUID();
        ResultActions resultActions = performFollow(buyerId, seller.getId());
        assertBadRequestWithMessage(resultActions, "Buyer " + buyerId + " not found");
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
        assertBadRequestWithMessage(resultActions, "Buyer " + buyer.getId() + " is not following seller "
                + seller.getId() + ". Unfollow action cannot be done");
    }

    @Test
    @DisplayName("[ERROR] Unfollow seller - seller not found")
    void testUnfollowSellerThatDoesNotExistError() throws Exception {
        UUID sellerId = UUID.randomUUID();
        ResultActions resultActions = performUnfollow(buyer.getId(), sellerId);
        assertBadRequestWithMessage(resultActions, "Seller " + sellerId + " not found");
    }

    @Test
    @DisplayName("[ERROR] Unfollow seller - buyer not found")
    void testUnfollowSellerButBuyerNotFoundError() throws Exception {
        UUID buyerId = UUID.randomUUID();
        ResultActions resultActions = performUnfollow(buyerId, seller.getId());
        assertBadRequestWithMessage(resultActions, "Buyer " + buyerId + " not found");
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