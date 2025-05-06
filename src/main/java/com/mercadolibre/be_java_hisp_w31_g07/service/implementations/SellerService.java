package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.ISellerService;
import com.mercadolibre.be_java_hisp_w31_g07.util.ErrorMessagesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SellerService implements ISellerService {

    private final ISellerRepository sellerRepository;

    @Override
    public void addBuyerToFollowers(Buyer buyer, Seller seller) {
        sellerRepository.addBuyerToFollowersList(buyer, seller.getId());
    }

    @Override
    public void removeBuyerFromFollowersList(Buyer buyer, UUID sellerId) {
        sellerRepository.removeBuyerFromFollowersList(buyer, sellerId);
    }

    @Override
    public boolean isSellerFollowedByBuyer(Buyer buyer, UUID sellerId) {
        return sellerRepository.sellerIsBeingFollowedByBuyer(buyer, sellerId);
    }

    @Override
    public Seller findSellerById(UUID sellerId) {
        return sellerRepository.findSellerById(sellerId)
                .orElseThrow(() -> new BadRequest(ErrorMessagesUtil.sellerNotFound(sellerId)));
    }

}