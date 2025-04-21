package com.mercadolibre.be_java_hisp_w31_g07.repository;

import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;

import java.util.Optional;
import java.util.UUID;

public interface IBuyerRepository {
    /**
     * Returns a buyer that match with the param userId.
     *
     * @param userId id to find a buyer.
     * @return a buyer with his followed list if the buyer is found,
     *         or an empty Optional otherwise.
     */
    Optional<Buyer> findFollowed(UUID userId);
}
