package com.mercadolibre.be_java_hisp_w31_g07.util;

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
}

