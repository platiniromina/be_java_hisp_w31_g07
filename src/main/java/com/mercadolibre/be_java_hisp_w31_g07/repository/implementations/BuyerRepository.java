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
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IBuyerRepository;

@Repository
public class BuyerRepository implements IBuyerRepository {
    private List<Buyer> buyerList = new ArrayList<>();

    public BuyerRepository() throws IOException {
        loadDataBaseBuyer();
    }

    private void loadDataBaseBuyer() throws IOException {
        File file;
        ObjectMapper objectMapper = new ObjectMapper();
        List<Buyer> buyers;

        file = ResourceUtils.getFile("classpath:buyer.json");
        buyers = objectMapper.readValue(file, new TypeReference<List<Buyer>>() {
        });

        buyerList = buyers;
    }

    @Override
    public Optional<Buyer> findFollowed(UUID userId) {
        return buyerList.stream()
                .filter(buyer -> buyer.getId().equals(userId))
                .findFirst();
    }

    @Override
    public Optional<Buyer> addSellerToFollowedList(Seller seller, UUID buyerId) {
        return buyerList.stream()
                .filter(buyer -> buyer.getId().equals(buyerId))
                .findFirst()
                .map(buyer -> {
                    buyer.addFollowedSeller(seller);
                    return buyer;
                });
    }

    @Override
    public Optional<Buyer> findBuyerById(UUID userId) {
        return buyerList.stream()
                .filter(buyer -> buyer.getId().equals(userId))
                .findFirst();
    }

    @Override
    public boolean buyerIsFollowingSeller(Seller seller, UUID buyerId) {
        return buyerList.stream()
                .filter(buyer -> buyer.getId().equals(buyerId))
                .flatMap(buyer -> buyer.getFollowed().stream())
                .anyMatch(sellerFollowed -> sellerFollowed.getId().equals(seller.getId()));
    }

    @Override
    public void removeSellerFromFollowedList(Seller seller, UUID buyerId) {
        this.findBuyerById(buyerId).map(buyer -> {
            buyer.removeFollowedSeller(seller);
            return buyer;
        });
    }
}
