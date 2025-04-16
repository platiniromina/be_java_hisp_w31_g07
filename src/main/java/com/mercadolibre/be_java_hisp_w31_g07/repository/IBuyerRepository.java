package com.mercadolibre.be_java_hisp_w31_g07.repository;

import java.util.Optional;
import java.util.UUID;

import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;

public interface IBuyerRepository {
    public Optional<Buyer> findBuyerById(UUID userId);

    public Optional<Seller> followSeller(Seller seller, UUID buyerId);
}
