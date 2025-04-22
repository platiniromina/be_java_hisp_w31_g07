package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.model.Product;
import com.mercadolibre.be_java_hisp_w31_g07.repository.implementations.ProductRepository;

public interface IProductService {

    /**
     * Adds a new product to the repository.
     *
     * This method delegates the task of storing the given {@link Product}
     * instance to the underlying product repository. It calls the
     * {@link ProductRepository#createProduct(Product)} method to perform the
     * actual creation of the product.
     *
     * @param product the {@link Product} instance to be created and stored in the repository.
     */
    public void createProduct(Product product);
}
