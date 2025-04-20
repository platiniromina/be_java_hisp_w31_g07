package com.mercadolibre.be_java_hisp_w31_g07.repository;

import java.util.Optional;
import java.util.UUID;

public interface ISellerRepository {
    /**
     * Retrieves the count of followers for a specific seller identified by their
     * UUID.
     *
     * @param sellerId the unique identifier of the seller whose follower count is
     *                 to be retrieved.
     * @return an Optional Integer containing the follower count if the seller is
     *         found,
     *         or an empty Optional otherwise.
     */
    public Optional<Integer> findFollowersCount(UUID sellerId);
}
