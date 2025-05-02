package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SellerFactory {

    private static final int DEFAULT_FOLLOWER_COUNT = 0;

    public static Seller createSeller() {
        Seller seller = new Seller();
        seller.setId(UUID.randomUUID());
        seller.setFollowerCount(DEFAULT_FOLLOWER_COUNT);
        seller.setFollowers(new ArrayList<>());
        return seller;
    }

    public static Seller createSellerWithFollowers(int followerCount) {
        Seller seller = createSeller();
        List<Buyer> followers = generateFollowers(followerCount);
        seller.setFollowers(followers);
        seller.setFollowerCount(followerCount);
        return seller;
    }

    // --- Helper Method ---

    private static List<Buyer> generateFollowers(int count) {
        List<Buyer> followers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            followers.add(BuyerFactory.createBuyer());
        }
        return followers;
    }
}



