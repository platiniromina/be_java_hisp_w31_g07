package com.mercadolibre.be_java_hisp_w31_g07.repository;

import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import java.util.Optional;
import java.util.UUID;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;

public interface IBuyerRepository {
    /**
     * Returns a buyer that match with the param userId.
     *
     * @param userId id to find a buyer.
     * @return a buyer with his followed list if the buyer is found,
     *         or an empty Optional otherwise.
     */
    public Optional<Buyer> findFollowed(UUID userId);

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
     * Checks if a buyer is following a specific seller.
     *
     * @param seller  The seller to check if they are being followed.
     * @param buyerId The unique identifier of the buyer.
     * @return {@code true} if the buyer is following the seller, {@code false}
     *         otherwise.
     */
    public boolean buyerIsFollowingSeller(Seller seller, UUID buyerId);

    /**
     * Removes the seller from the buyer's followed list.
     *
     * @param seller The seller to be removed from the buyer's followed list.
     * @param buyerId The unique identifier of the buyer.
     */
    public void removeSellerFromFollowedList(Seller seller, UUID buyerId);
}
