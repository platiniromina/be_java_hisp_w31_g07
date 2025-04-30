package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BuyerFactory {

    public static Buyer createBuyer() {
        Buyer buyer = new Buyer();
        buyer.setId(UUID.randomUUID());
        buyer.setFollowed(new ArrayList<>());
        return buyer;
    }

    public static Buyer createBuyerWithFollowed(List<Seller> sellers) {
        Buyer buyer = createBuyer();
        buyer.setFollowed(sellers);
        return buyer;
    }

    public static Buyer createBuyerFollowing(Seller... sellers) {
        Buyer buyer = createBuyer();
        for (Seller seller : sellers) {
            buyer.addFollowedSeller(seller);
        }
        return buyer;
    }
}

