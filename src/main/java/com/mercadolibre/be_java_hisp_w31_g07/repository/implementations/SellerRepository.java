package com.mercadolibre.be_java_hisp_w31_g07.repository.implementations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;

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
    public Optional<Seller> findFollowers(UUID userId) {
        return sellerList.stream()
                .filter(seller -> seller.getId().equals(userId))
                .findFirst();
    }

    @Override
    public Optional<Seller> addBuyerToFollowersList(Buyer buyer, UUID sellerId) {
        return sellerList.stream()
                .filter(seller -> seller.getId().equals(sellerId))
                .findFirst()
                .map(seller -> {
                    seller.addFollower(buyer);
                    seller.incrementFollowerCount();
                    return seller;
                });
    }

    @Override
    public Optional<Seller> findSellerById(UUID userId) {
        return sellerList.stream()
                .filter(seller -> seller.getId().equals(userId))
                .findFirst();
    }

    @Override
    public boolean sellerIsBeingFollowedByBuyer(Buyer buyer, UUID sellerId) {
        return sellerList.stream()
                .filter(seller -> seller.getId().equals(sellerId))
                .flatMap(seller -> seller.getFollowers().stream())
                .anyMatch(follower -> follower.getId().equals(buyer.getId()));
    }

    @Override
    public void removeBuyerFromFollowersList(Buyer buyer, UUID sellerId) {
        this.findSellerById(sellerId).map(seller -> {
            seller.removeFollower(buyer);
            seller.decrementFollowerCount();
            return seller;
        });
    }
}
