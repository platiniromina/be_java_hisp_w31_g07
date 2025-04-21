package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;

import java.util.Optional;
import java.util.UUID;

public interface ISellerService {

    Seller findSellerById(UUID sellerId);
}
