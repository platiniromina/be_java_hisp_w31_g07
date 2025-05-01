package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;

import java.util.ArrayList;
import java.util.UUID;

public class SellerFactory {

    public static Seller createSeller() {
        Seller seller = new Seller();
        seller.setId(UUID.randomUUID());
        seller.setFollowerCount(0);
        seller.setFollowers(new ArrayList<>());
        return seller;
    }

    public static SellerResponseDto createSellerResponseDto(UUID sellerId) {
        SellerResponseDto seller = new SellerResponseDto();
        seller.setId(sellerId);
        seller.setFollowerCount(0);
        seller.setFollowers(new ArrayList<>());
        return seller;
    }

    public static Seller createSellerWithFollowers(int followerCount) {
        Seller seller = createSeller();
        for (int i = 0; i < followerCount; i++) {
            seller.addFollower(BuyerFactory.createBuyer());
        }
        seller.setFollowerCount(followerCount);
        return seller;
    }
}

