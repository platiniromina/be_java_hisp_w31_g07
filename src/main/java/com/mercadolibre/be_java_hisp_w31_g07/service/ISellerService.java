package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;

import java.util.UUID;

public interface ISellerService {

    /**
     * Retrieves a seller by their unique identifier.
     *
     * @param sellerId the ID of the seller to retrieve
     * @return the found seller
     * @throws BadRequest if no seller is found with the given ID
     */
    Seller findSellerById(UUID sellerId);

    /**
     * Adds a buyer to the list of followers of the given seller.
     *
     * @param buyer  the buyer who will follow the seller
     * @param seller the seller to be followed
     */
    void addBuyerToFollowers(Buyer buyer, Seller seller);

    /**
     * Checks if a given seller is currently followed by a specific buyer.
     *
     * @param buyer    the buyer in question
     * @param sellerId the ID of the seller to check
     * @return true if the buyer is following the seller, false otherwise
     */
    boolean isSellerFollowedByBuyer(Buyer buyer, UUID sellerId);

    /**
     * Removes a buyer from the list of followers of the given seller.
     *
     * @param buyer    the buyer who will stop following the seller
     * @param sellerId the ID of the seller to be unfollowed
     */
    void removeBuyerFromFollowersList(Buyer buyer, UUID sellerId);

}
