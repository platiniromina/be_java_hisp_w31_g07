package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IBuyerService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IUserService;
import com.mercadolibre.be_java_hisp_w31_g07.util.BuyerFactory;
import com.mercadolibre.be_java_hisp_w31_g07.util.SellerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SellerControllerTest {

    @MockitoBean
    private ISellerRepository sellerRepository;

    @MockitoBean
    private IUserService userService;

    @MockitoBean
    private IBuyerService buyerService;

    @Autowired
    private MockMvc mockMvc;

    private Seller seller;
    private UUID sellerId;
    private Buyer buyer;
    private UUID buyerId;

    @BeforeEach
    void setUp() {
        seller = SellerFactory.createSeller();
        sellerId = seller.getId();
        buyer = BuyerFactory.createBuyer();
        buyerId = buyer.getId();
    }

    @Test
    @DisplayName("[SUCCESS] Follow a seller")
    void testFollowSellerSuccess() throws Exception {
        stubValidBuyerAndSeller();

        ResultActions resultActions = performFollow(buyerId, sellerId);

        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("[ERROR] Follow a seller with invalid buyerId")
    void testFollowSellerSameUserError() throws Exception {
        stubValidBuyerAndSeller();

        ResultActions resultActions = performFollow(buyerId, buyerId);  // Following himself

        assertBadRequestWithMessage(resultActions, "User cannot follow themselves.");
    }

    @Test
    @DisplayName("[ERROR] Follow a seller with invalid sellerId")
    void testFollowSellerAlreadyFollowingError() throws Exception {
        seller.addFollower(buyer);
        buyer.addFollowedSeller(seller);
        stubValidBuyerAndSeller();
        when(buyerService.buyerIsFollowingSeller(seller, buyerId)).thenReturn(true);
        when(sellerRepository.sellerIsBeingFollowedByBuyer(buyer, sellerId)).thenReturn(true);

        ResultActions resultActions = performFollow(buyerId, sellerId);

        assertBadRequestWithMessage(resultActions, "Buyer " + buyerId + " already follows seller " + sellerId);
    }

    @Test
    @DisplayName("[ERROR] Follow a seller with invalid buyerId")
    void testFollowSellerThatDoesNotExistError() throws Exception {
        when(buyerService.findBuyerById(buyerId)).thenReturn(buyer);
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.empty());

        ResultActions resultActions = performFollow(buyerId, sellerId);

        assertBadRequestWithMessage(resultActions, "Seller " + sellerId + " not found");
    }

    @Test
    @DisplayName("[ERROR] Follow a seller with invalid buyerId")
    void testFollowSellerButBuyerNotFoundError() throws Exception {
        when(buyerService.findBuyerById(buyerId)).thenThrow(new BadRequest("Buyer " + buyerId + " not found"));
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.of(seller));

        ResultActions resultActions = performFollow(buyerId, sellerId);

        assertBadRequestWithMessage(resultActions, "Buyer " + buyerId + " not found");
    }

    private ResultActions performFollow(UUID buyerId, UUID sellerId) throws Exception {
        return mockMvc.perform(
                post("/users/{buyerId}/follow/{sellerId}", buyerId, sellerId)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());
    }

    private void stubValidBuyerAndSeller() {
        when(buyerService.findBuyerById(buyerId)).thenReturn(buyer);
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.of(seller));
    }

    private void assertBadRequestWithMessage(ResultActions resultActions, String expectedMessage) throws Exception {
        resultActions.andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertEquals(expectedMessage,
                                Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
}