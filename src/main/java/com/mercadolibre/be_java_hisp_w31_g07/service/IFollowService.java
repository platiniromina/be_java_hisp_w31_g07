package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.SellerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerFollowersCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;

import java.util.UUID;

public interface IFollowService {

    /**
     * Allows a buyer to follow a seller.
     *
     * @param sellerId the ID of the seller to be followed
     * @param buyerId  the ID of the buyer who wants to follow
     * @throws BadRequest if the buyer is already following the seller or if the IDs are the same
     */
    void follow(UUID sellerId, UUID buyerId);

    /**
     * Allows a buyer to unfollow a seller.
     *
     * @param sellerId the ID of the seller to be unfollowed
     * @param buyerId  the ID of the buyer who wants to unfollow
     * @throws BadRequest if the buyer is not currently following the seller or if the IDs are the same
     */
    void unfollow(UUID sellerId, UUID buyerId);

    /**
     * Returns a seller's followers sorted alphabetically by name.
     *
     * @param sellerId the ID of the seller whose followers will be retrieved
     * @param order    the sort order ("asc" or "desc")
     * @return a SellerDto containing the sorted list of followers and seller information
     */
    SellerDto findSortedFollowersByName(UUID sellerId, String order);

    /**
     * Returns the total number of followers a seller has.
     *
     * @param sellerId the ID of the seller
     * @return a DTO containing the seller ID, username, and number of followers
     */
    SellerFollowersCountResponseDto findFollowersCount(UUID sellerId);

    /**
     * Retrieves all followers of a seller without sorting.
     *
     * @param sellerId the ID of the seller
     * @return a SellerDto containing the list of followers and seller information
     */
    SellerDto findFollowers(UUID sellerId);

    /**
     * Retrieves the list of sellers followed by a specific buyer.
     *
     * @param buyerId the ID of the buyer
     * @return a BuyerDto containing the buyer's followed sellers and username
     */
    BuyerDto findFollowed(UUID buyerId);
}
