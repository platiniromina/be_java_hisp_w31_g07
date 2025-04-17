package com.mercadolibre.be_java_hisp_w31_g07.repository;

import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;

import java.util.Optional;
import java.util.UUID;

public interface IBuyerRepository {
    Optional<Buyer> findFollowed(UUID userId);
}
