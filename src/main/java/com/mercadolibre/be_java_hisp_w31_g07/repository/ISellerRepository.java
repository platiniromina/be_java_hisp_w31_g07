package com.mercadolibre.be_java_hisp_w31_g07.repository;

import java.util.Optional;
import java.util.UUID;

import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;

public interface ISellerRepository {
    /**
     * Retrieves a Seller from the list based on their unique ID.
     *
     * @param userId the UUID of the seller to be found
     * @return an Optional containing the Seller if found, or an empty Optional if
     *         no
     *         match is found
     */
    public Optional<Seller> findSellerById(UUID userId);

    /**
     * Adds the given buyer to the seller's list of followers.
     *
     * @param buyer    the buyer who wants to follow the seller
     * @param sellerId the ID of the seller to be followed
     * @return an Optional containing the updated Seller if found, or empty if no
     *         seller with the given ID exists
     */
    public Optional<Seller> addBuyerToFollowersList(Buyer buyer, UUID sellerId);

    /**
     * Checks if a buyer is already following a seller.
     *
     * @param buyer    the buyer to check
     * @param sellerId the ID of the seller to check against
     * @return true if the buyer is following the seller, false otherwise
     */
    public boolean buyerIsFollowingSeller(Buyer buyer, UUID sellerId);
}
