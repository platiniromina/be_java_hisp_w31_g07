package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SellerFactory {

    public static Seller createSeller() {
        Seller seller = new Seller();
        seller.setId(UUID.randomUUID());
        seller.setFollowerCount(0);
        seller.setFollowers(new ArrayList<>());
        return seller;
    }

    public static Seller createSellerWithFollowers(List<Buyer> buyers) {
        Seller seller = createSeller();
        for (Buyer buyer : buyers) {
            seller.addFollower(buyer);
        }
        seller.setFollowerCount(buyers.size());
        return seller;
    }

    public static Seller createSellerWithFollowers(Buyer... buyers) {
        return createSellerWithFollowers(Arrays.asList(buyers));
    }
}

