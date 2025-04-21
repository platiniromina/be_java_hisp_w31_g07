package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.SellerDto;
import java.util.UUID;

public interface ISellerService {
    /**
     * Returns a seller that match with the param userId.
     *
     * @param userId id to find the Seller.
     * @return a seller dto with his followers list.
     */
    SellerDto findFollowers(UUID userId);
}
