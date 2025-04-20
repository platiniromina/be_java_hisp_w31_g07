package com.mercadolibre.be_java_hisp_w31_g07.repository.implementations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;

@Repository
public class SellerRepository implements ISellerRepository {
    private List<Seller> sellerList = new ArrayList<>();

    public SellerRepository() throws IOException {
        loadDataBaseSeller();
    }

    private void loadDataBaseSeller() throws IOException {
        File file;
        ObjectMapper objectMapper = new ObjectMapper();
        List<Seller> sellers;

        file = ResourceUtils.getFile("classpath:seller.json");
        sellers = objectMapper.readValue(file, new TypeReference<List<Seller>>() {
        });

        sellerList = sellers;
    }

    @Override
    public Optional<Integer> findFollowersCount(UUID sellerId) {
        return sellerList.stream()
                .filter(seller -> seller.getId().equals(sellerId))
                .findFirst()
                .map(Seller::getFollowerCount);
    }
}
