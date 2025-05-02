package com.mercadolibre.be_java_hisp_w31_g07.repository;

import com.mercadolibre.be_java_hisp_w31_g07.model.Product;

public interface IProductRepository {

    /**
     * Adds a new product to the internal repository.
     * <p>
     * This method is responsible for storing the given {@link Product}
     * instance in an internal list of products. By calling this method,
     * the provided product will be added to the collection, making it
     * accessible for future retrievals or manipulations.
     *
     * @param product the {@link Product} instance to be created and stored in the internal repository.
     */
    void createProduct(Product product);
}
