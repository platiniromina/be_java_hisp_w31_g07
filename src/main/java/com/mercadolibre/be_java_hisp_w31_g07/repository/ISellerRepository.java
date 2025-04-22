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
     * Checks if a given seller is being followed by a specific buyer.
     *
     * @param buyer    The buyer whose following status is to be checked.
     * @param sellerId The unique identifier of the seller.
     * @return {@code true} if the seller is being followed by the buyer,
     *         {@code false} otherwise.
     */
    public boolean sellerIsBeingFollowedByBuyer(Buyer buyer, UUID sellerId);

    /**
     * Removes the buyer from the seller's list of followers.
     *
     * @param buyer    The buyer to be removed from the seller's list of followers.
     * @param sellerId The unique identifier of the seller.
     */
    public void removeBuyerFromFollowersList(Buyer buyer, UUID sellerId);
}
