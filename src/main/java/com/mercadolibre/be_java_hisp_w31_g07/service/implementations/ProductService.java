package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.mercadolibre.be_java_hisp_w31_g07.model.Product;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IPostRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IProductRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final IProductRepository productRepository;

    @Override
    public void createProduct(Product product) {
        productRepository.createProduct(product);
    }
}
