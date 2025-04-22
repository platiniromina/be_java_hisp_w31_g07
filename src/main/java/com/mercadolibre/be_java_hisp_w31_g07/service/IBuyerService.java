package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import java.util.UUID;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;

public interface IBuyerService {
    /**
     * Returns a buyer that match with the param userId.
     *
     * @param userId id to find a buyer.
     * @return a buyer with his followed list if the buyer is found,
     *         or an Exception.
     */
    public BuyerDto findFollowed(UUID userId);

    /**
     * Adds a seller to the followed list of a buyer.
     *
     * @param seller  The seller to be added to the buyer's followed list.
     * @param buyerId The unique identifier of the buyer.
     * @throws BadRequest If the buyer with the specified ID is not found.
     */
    public void addSellerToFollowedList(Seller seller, UUID buyerId);

    /**
     * Checks if a buyer is following a specific seller.
     *
     * @param seller  The seller to check if the buyer is following.
     * @param buyerId The unique identifier of the buyer.
     * @return {@code true} if the buyer is following the seller, {@code false}
     *         otherwise.
     */
    public boolean buyerIsFollowingSeller(Seller seller, UUID buyerId);

    /**
     * Retrieves a buyer by their unique identifier.
     *
     * @param buyerId the unique identifier of the buyer to be retrieved
     * @return a Buyer object containing the buyer's information
     * @throws BadRequest if the buyer cannot be found
     */
    public Buyer findBuyerById(UUID buyerId);

    /**
     * Removes the seller from the buyer's followed list
     *
     * @param seller The seller to be removed from the buyer's followed list
     * @param buyerId The unique identifier of the buyer.
     */
    public void removeSellerFromFollowedList(Seller seller, UUID buyerId);

}
