package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.model.User;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.implementations.UserRepository;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FollowControllerTest {
    @Autowired
    private ISellerRepository sellerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    private Seller sellerWithFollowers;
    private User userWithFollowers;

    @BeforeEach
    void setUp() {
        sellerWithFollowers = SellerFactory.createSellerWithFollowers(3);
        userWithFollowers = UserFactory.createUser(sellerWithFollowers.getId());
        sellerRepository.save(sellerWithFollowers);
        userRepository.save(userWithFollowers);
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
}