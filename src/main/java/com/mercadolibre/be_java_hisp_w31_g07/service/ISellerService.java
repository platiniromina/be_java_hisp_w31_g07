package com.mercadolibre.be_java_hisp_w31_g07.service;

import java.util.UUID;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerFollowersCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;

public interface ISellerService {
    /**
     * Retrieves the count of followers for a given seller and constructs a response
     * DTO.
     *
     * @param sellerId the unique identifier of the seller whose followers count is
     *                 to be retrieved
     * @return a SellerFollowersCountResponseDto containing the seller's ID,
     *         username, and followers count
     * @throws BadRequest if the seller with the given ID is not found
     */
    public SellerFollowersCountResponseDto findFollowersCount(UUID userId);
}
