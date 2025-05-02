package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IBuyerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IBuyerService;
import com.mercadolibre.be_java_hisp_w31_g07.util.ErrorMessagesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuyerService implements IBuyerService {

    private final IBuyerRepository buyerRepository;

    @Override
    public Buyer findBuyerById(UUID buyerId) {
        return buyerRepository.findBuyerById(buyerId)
                .orElseThrow(() -> new BadRequest(ErrorMessagesUtil.buyerNotFound(buyerId)));
    }

    @Override
    public void addSellerToFollowed(Seller seller, Buyer buyer) {
        buyerRepository.addSellerToFollowedList(seller, buyer.getId())
                .orElseThrow(() -> new BadRequest(ErrorMessagesUtil.sellerNotFound(seller.getId())));
    }

    @Override
    public boolean isBuyerFollowingSeller(Seller seller, UUID buyerId) {
        return buyerRepository.buyerIsFollowingSeller(seller, buyerId);
    }

    @Override
    public void removeSellerFromFollowedList(Seller seller, UUID buyerId) {
        buyerRepository.removeSellerFromFollowedList(seller, buyerId);
    }

}
