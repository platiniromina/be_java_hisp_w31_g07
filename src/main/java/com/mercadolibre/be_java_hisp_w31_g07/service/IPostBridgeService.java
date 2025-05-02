package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Product;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;

import java.util.List;
import java.util.UUID;

public interface IPostBridgeService {

    /**
     * Retrieves the username of a user by their ID.
     *
     * @param userId the ID of the user
     * @return the username associated with the given user ID
     */
    String getUserName(UUID userId);

    /**
     * Delegates the creation of a new product.
     *
     * @param product the product to be created
     */
    void createProduct(Product product);

    /**
     * Retrieves full user information by user ID.
     *
     * @param userId the ID of the user
     * @return a UserDto containing user data
     */
    UserDto getUserById(UUID userId);

    /**
     * Retrieves the list of sellers followed by a specific buyer.
     *
     * @param buyerId the ID of the buyer
     * @return a list of followed sellers
     */
    List<Seller> getFollowed(UUID buyerId);

    /**
     * Validates that a seller with the given ID exists.
     *
     * @param sellerId the ID of the seller to validate
     * @throws BadRequest if the seller does not exist
     */
    void validateSellerExists(UUID sellerId);
}


