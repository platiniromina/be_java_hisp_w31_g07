package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;

import java.util.UUID;

public interface IBuyerService {
    /**
     * Returns a buyer that match with the param userId.
     *
     * @param userId id to find a buyer.
     * @return a buyer with his followed list if the buyer is found,
     *         or an Exception.
     */
    BuyerDto findFollowed(UUID userId);
}
