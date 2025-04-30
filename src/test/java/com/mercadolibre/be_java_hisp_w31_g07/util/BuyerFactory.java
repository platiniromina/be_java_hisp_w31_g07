package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;

import java.util.ArrayList;
import java.util.UUID;

public class BuyerFactory {
    public static Buyer createBuyer() {
        Buyer buyer = new Buyer();
        buyer.setId(UUID.randomUUID());
        buyer.setFollowed(new ArrayList<>());
        return buyer;
    }
}
