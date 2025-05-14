package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.model.Product;

public interface IProductService {

    /**
     * Persists a new product in the repository.
     *
     * @param product the Product entity to be created
     */
    void createProduct(Product product);
}

