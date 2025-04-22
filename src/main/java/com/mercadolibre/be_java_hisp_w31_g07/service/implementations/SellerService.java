package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.SellerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.BuyerReponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerFollowersCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IBuyerService;
import com.mercadolibre.be_java_hisp_w31_g07.service.ISellerService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerService implements ISellerService {
    private final ISellerRepository sellerRepository;
    private final IUserService userService;
    private final IBuyerService buyerService;

    // ------------------------------
    // Public methods
    // ------------------------------
    @Override
    public void followSeller(UUID sellerId, UUID buyerId) {
        validateNotSameUser(sellerId, buyerId);
        Seller seller = getSellerById(sellerId);
        Buyer buyer = buyerService.findBuyerById(buyerId);

        if (this.validateMutualFollowing(buyer, seller))
            throw new BadRequest("Buyer " + buyer.getId() + " already follows seller " + seller.getId());

        sellerRepository.addBuyerToFollowersList(buyer, sellerId);
        buyerService.addSellerToFollowedList(seller, buyerId);
    }

    @Override
    public SellerDto findFollowers(UUID userId) {
        Seller seller = sellerRepository.findSellerById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        return mapToDto(seller);
    }

    @Override
    public void unfollowSeller(UUID sellerId, UUID buyerId) {
        Seller seller = this.getSellerById(sellerId);
        Buyer buyer = buyerService.findBuyerById(buyerId);

        if (!this.validateMutualFollowing(buyer, seller))
            throw new BadRequest("Buyer " + buyer.getId() + " is not following seller " + seller.getId()
                    + ". Unfollow action cannot be done");

        sellerRepository.removeBuyerFromFollowersList(buyer, sellerId);
        buyerService.removeSellerFromFollowedList(seller, buyerId);
    }

    @Override
    public SellerFollowersCountResponseDto findFollowersCount(UUID sellerId) {
        Seller seller = this.getSellerById(sellerId);
        Integer count = seller.getFollowerCount();
        String userName = userService.findById(sellerId).getUserName();
        return new SellerFollowersCountResponseDto(sellerId, userName, count);
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

    private SellerDto mapToDto(Seller seller) {
        ObjectMapper mapper = new ObjectMapper();

        List<BuyerReponseDto> followers = seller.getFollowers().stream()
                .map(buyer -> {
                    BuyerReponseDto buyerReponseDto = mapper.convertValue(buyer, BuyerReponseDto.class);
                    UserDto user = userService.findById(buyer.getId());
                    buyerReponseDto.setUserName(user.getUserName());
                    return buyerReponseDto;
                })
                .toList();

        return new SellerDto(
                seller.getId(),
                userService.findById(seller.getId()).getUserName(),
                followers,
                seller.getFollowerCount());
    }

    // ------------------------------
    // Validation methods
    // ------------------------------
    private void validateNotSameUser(UUID sellerId, UUID buyerId) {
        if (sellerId.equals(buyerId))
            throw new BadRequest("User cannot follow themselves.");
    }

    private boolean validateMutualFollowing(Buyer buyer, Seller seller) {
        boolean isFollowedBy = sellerRepository.sellerIsBeingFollowedByBuyer(buyer, seller.getId());
        boolean isFollowing = buyerService.buyerIsFollowingSeller(seller, buyer.getId());
        return isFollowing && isFollowedBy;
    }

}
