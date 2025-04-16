package com.mercadolibre.be_java_hisp_w31_g07.repository.implementations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.model.Product;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IProductRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepository implements IProductRepository {
    private List<Product> productList = new ArrayList<>();

    public ProductRepository() throws IOException {
        loadDataBaseProduct();
    }

    private void loadDataBaseProduct() throws IOException {
        File file;
        ObjectMapper objectMapper = new ObjectMapper();
        List<Product> products;

        file= ResourceUtils.getFile("");
        products= objectMapper.readValue(file,new TypeReference<List<Product>>(){});

        productList = products;
    }
}
