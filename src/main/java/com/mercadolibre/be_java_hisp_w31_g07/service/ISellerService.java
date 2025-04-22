package com.mercadolibre.be_java_hisp_w31_g07.service;

import java.util.UUID;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.SellerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerFollowersCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.UserPostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;

public interface ISellerService {
    /**
     * Retrieves the count of followers for a given seller and constructs a response
     * DTO.
     *
     * @param userId the unique identifier of the seller whose followers count is
     *                 to be retrieved
     * @return a SellerFollowersCountResponseDto containing the seller's ID,
     *         username, and followers count
     * @throws BadRequest if the seller with the given ID is not found
     */
    public SellerFollowersCountResponseDto findFollowersCount(UUID userId);

    /**
     * Returns a seller that match with the param userId.
     *
     * @param userId id to find the Seller.
     * @return a seller dto with his followers list.
     */
    public SellerDto findFollowers(UUID userId);

    /**
     * Allows a buyer to follow a seller by adding the seller to the buyer's
     * followed list
     * and the buyer to the seller's followers list.
     *
     * @param sellerId the unique identifier of the seller to be followed
     * @param buyerId the unique identifier of the buyer who wants to follow the
     *                 seller
     * @throws BadRequest if the sellerId and buyerId are the same
     * @throws BadRequest if the seller or buyer cannot be found
     * @throws BadRequest if the buyer is already following the seller
     */
    public void followSeller(UUID sellerId, UUID buyerId);

    /**
     * Allows a buyer to unfollow a seller by removing the seller from the buyer's
     * followed list
     * and the buyer from the seller's followers list.
     *
     * @param sellerId the unique identifier of the seller to be unfollowed
     * @param buyerId  the unique identifier of the buyer who wants to unfollow the
     *                 seller
     * @throws BadRequest if the seller or buyer cannot be found
     * @throws BadRequest if the buyer is not previously following the seller
     */
    public void unfollowSeller(UUID sellerId, UUID buyerId);

    /**
     * Retrieves a seller by their unique identifier.
     *
     * @param userId the unique identifier of the seller to be retrieved
     * @return a Seller object containing the seller's information
     * @throws BadRequest if the seller cannot be found
     */
    public Seller findSellerById(UUID userId);

}
