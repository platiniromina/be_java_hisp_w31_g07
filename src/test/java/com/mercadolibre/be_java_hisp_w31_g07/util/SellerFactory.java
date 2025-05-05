package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.BuyerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SellerFactory {

    private static final int DEFAULT_FOLLOWER_COUNT = 0;
    private static final String DEFAULT_USER_NAME = "TEST USERNAME";

    public static Seller createSeller(UUID sellerId) {
        Seller seller = new Seller();
        seller.setId(sellerId != null ? sellerId : UUID.randomUUID());
        seller.setFollowerCount(DEFAULT_FOLLOWER_COUNT);
        seller.setFollowers(new ArrayList<>());
        return seller;
    }

    public static Seller createSellerWithFollowers(int followerCount) {
        Seller seller = createSeller(null);
        List<Buyer> followers = generateFollowers(followerCount);
        seller.setFollowers(followers);
        seller.setFollowerCount(followerCount);
        return seller;
    }

    public static SellerResponseDto createSellerResponseDto(UUID sellerId) {
        SellerResponseDto seller = new SellerResponseDto();
        seller.setId(sellerId);
        seller.setFollowerCount(0);
        seller.setFollowers(new ArrayList<>());
        return seller;
    }

    public static SellerResponseDto createSellerResponseDtoFollowers(UUID sellerId, BuyerResponseDto follower) {
        SellerResponseDto seller = new SellerResponseDto();
        seller.setId(sellerId);
        seller.setFollowerCount(1);
        seller.setFollowers(List.of(follower));
        seller.setUserName(DEFAULT_USER_NAME);
        return seller;
    }

    // --- Helper Method ---

    private static List<Buyer> generateFollowers(int count) {
        List<Buyer> followers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            followers.add(BuyerFactory.createBuyer(null));
        }
        return followers;
    }
}

