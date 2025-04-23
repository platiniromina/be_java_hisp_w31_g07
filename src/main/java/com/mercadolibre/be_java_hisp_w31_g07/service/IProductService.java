package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.UserPostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Product;
import com.mercadolibre.be_java_hisp_w31_g07.repository.implementations.ProductRepository;

import java.util.UUID;

public interface IProductService {

    /**
     * Adds a new product to the repository.
     *
     * This method delegates the task of storing the given {@link Product}
     * instance to the underlying product repository. It calls the
     * {@link ProductRepository#createProduct(Product)} method to perform the
     * actual creation of the product.
     *
     * @param product the {@link Product} instance to be created and stored in the
     *                repository.
     */
    public void createProduct(Product product);

    /**
     * Retrieves a seller with a list of post with discount.
     *
     * @param userId the unique identifier of the seller to be retrieved
     * @return a UserPostReponseObject object containing the seller's information
     *         and post.
     * @throws BadRequest if the seller cannot be found
     */
    public UserPostResponseDto getSellerPromoPosts(UUID userId);
}
