package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IBuyerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.ISellerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerService implements ISellerService {
    private final ISellerRepository sellerRepository;
    private final IBuyerRepository buyerRepository;

    // ------------------------------
    // Public methods
    // ------------------------------
    @Override
    public void followSeller(UUID sellerId, UUID buyerId) {
        validateNotSameUser(sellerId, buyerId);
        Seller seller = getSellerById(sellerId);
        Buyer buyer = getBuyerById(buyerId);
        validateNotAlreadyFollowing(buyer, seller);
        sellerRepository.addBuyerToFollowersList(buyer, sellerId);
        buyerRepository.addSellerToFollowedList(seller, buyerId);
    }

    // FOR TESTING PURPOSES ONLY
    // This endpoint is not part of the original requirements
    // and is only used to verify the functionality of the followSeller method.
    // It should be removed in the final version of the code.
    @Override
    public Seller findSellerById(UUID sellerId) {
        return getSellerById(sellerId);
    }

    // FOR TESTING PURPOSES ONLY
    // This endpoint is not part of the original requirements
    // and is only used to verify the functionality of the followSeller method.
    // It should be removed in the final version of the code.
    @Override
    public Buyer findBuyerById(UUID buyerId) {
        return getBuyerById(buyerId);
    }

    // ------------------------------
    // Private methods
    // ------------------------------
    private Seller getSellerById(UUID id) {
        return sellerRepository.findSellerById(id)
                .orElseThrow(() -> new BadRequest("Seller " + id + " not found"));
    }

    private Buyer getBuyerById(UUID id) {
        return buyerRepository.findBuyerById(id)
                .orElseThrow(() -> new BadRequest("Buyer " + id + " not found"));
    }

    // ------------------------------
    // Validation methods
    // ------------------------------
    private void validateNotSameUser(UUID sellerId, UUID buyerId) {
        if (sellerId.equals(buyerId))
            throw new BadRequest("User cannot follow themselves.");
    }

    private void validateNotAlreadyFollowing(Buyer buyer, Seller seller) {
        boolean isFollowing = sellerRepository.buyerIsFollowingSeller(buyer, seller.getId());
        boolean isFollowedBy = buyerRepository.sellerIsFollowedByBuyer(seller, buyer.getId());
        if (isFollowing || isFollowedBy)
            throw new BadRequest("Buyer " + buyer.getId() + " already follows seller " + seller.getId());
    }
}
