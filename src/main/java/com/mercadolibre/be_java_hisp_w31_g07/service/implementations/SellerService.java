package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.ISellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SellerService implements ISellerService {
    private final ISellerRepository sellerRepository;

    @Override
    public Seller findSellerById(UUID sellerId) {
        return sellerRepository.findSellerById(sellerId)
                .orElseThrow(() -> new BadRequest("Seller " + sellerId + " not found"));
    }
}
