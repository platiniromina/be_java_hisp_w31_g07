package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class UserUtil {
    
    public static Map<UUID, String> initializeUsernameMap(Seller seller) {
        if (seller == null) return Collections.emptyMap();

        Map<UUID, String> usernameMap = new HashMap<>();
        usernameMap.put(seller.getId(), null);

        seller.getFollowers().forEach(buyer -> usernameMap.put(buyer.getId(), null));

        return usernameMap;
    }

}
