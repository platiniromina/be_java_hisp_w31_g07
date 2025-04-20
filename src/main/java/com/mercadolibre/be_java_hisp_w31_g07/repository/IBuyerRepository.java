package com.mercadolibre.be_java_hisp_w31_g07.repository;

import java.util.Optional;
import java.util.UUID;

import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;

public interface IBuyerRepository {
    /**
     * Retrieves a Buyer from the list based on their unique ID.
     *
     * @param userId the UUID of the buyer to be found
     * @return an Optional containing the Buyer if found, or an empty Optional if no
     *         match is found
     */
    public Optional<Buyer> findBuyerById(UUID userId);

    /**
     * Adds the given seller to the buyer's list of followed sellers.
     *
     * @param seller  the seller to be added to the followed list
     * @param buyerId the ID of the buyer who is following the seller
     * @return an Optional containing the updated Buyer if found, or an empty
     *         Optional if no
     *         match is found
     */
    public Optional<Buyer> addSellerToFollowedList(Seller seller, UUID buyerId);

    /**
     * Checks if a seller is already followed by a buyer.
     *
     * @param seller  the seller to check
     * @param buyerId the ID of the buyer to check against
     * @return true if the seller is followed by the buyer, false otherwise
     */
    public boolean sellerIsFollowedByBuyer(Seller seller, UUID buyerId);
}
