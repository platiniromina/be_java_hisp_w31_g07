package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IBuyerService;
import com.mercadolibre.be_java_hisp_w31_g07.service.ISellerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerService implements ISellerService {
    private final ISellerRepository sellerRepository;
    private final IBuyerService buyerService;

    // ------------------------------
    // Public methods
    // ------------------------------
    @Override
    public void followSeller(UUID sellerId, UUID buyerId) {
        validateNotSameUser(sellerId, buyerId);
        Seller seller = getSellerById(sellerId);
        Buyer buyer = buyerService.findBuyerById(buyerId);
        validateNotAlreadyFollowing(buyer, seller);
        sellerRepository.addBuyerToFollowersList(buyer, sellerId);
        buyerService.addSellerToFollowedList(seller, buyerId);
    }

    // FOR TESTING PURPOSES ONLY
    // This endpoint is not part of the original requirements
    // and is only used to verify the functionality of the followSeller method.
    // It should be removed in the final version of the code.
    @Override
    public Seller findSellerById(UUID sellerId) {
        return getSellerById(sellerId);
    }

    // ------------------------------
    // Private methods
    // ------------------------------
    private Seller getSellerById(UUID id) {
        return sellerRepository.findSellerById(id)
                .orElseThrow(() -> new BadRequest("Seller " + id + " not found"));
    }

    // ------------------------------
    // Validation methods
    // ------------------------------
    private void validateNotSameUser(UUID sellerId, UUID buyerId) {
        if (sellerId.equals(buyerId))
            throw new BadRequest("User cannot follow themselves.");
    }

    private void validateNotAlreadyFollowing(Buyer buyer, Seller seller) {
        boolean isFollowing = sellerRepository.sellerIsBeingFollowedByBuyer(buyer, buyer.getId());
        boolean isFollowedBy = buyerService.buyerIsFollowingSeller(seller, buyer.getId());
        if (isFollowing || isFollowedBy)
            throw new BadRequest("Buyer " + buyer.getId() + " already follows seller " + seller.getId());
    }
}
