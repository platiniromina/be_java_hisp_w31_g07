package com.mercadolibre.be_java_hisp_w31_g07.repository;

import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ISellerRepository {
    /**
     * Returns a seller that match with the param userId.
     *
     * @param userId id to find the Seller.
     * @return a seller with his followers list if the seller is found,
     *         or an empty Optional otherwise
     */
    Optional<Seller> findFollowers(UUID userId);
}
