package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.SellerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.BuyerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerAveragePrice;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerFollowersCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IBuyerService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IPostService;
import com.mercadolibre.be_java_hisp_w31_g07.service.ISellerService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IUserService;
import com.mercadolibre.be_java_hisp_w31_g07.util.BuyerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerService implements ISellerService {
    private final ISellerRepository sellerRepository;
    private final IUserService userService;
    private final IBuyerService buyerService;

    @Autowired
    @Lazy
    private IPostService postService;

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
    public SellerDto findFollowers(UUID sellerId) {
        Seller seller = sellerRepository.findSellerById(sellerId)
                .orElseThrow(() -> new BadRequest("Buyer: " + sellerId + " not found"));

        String buyerUserName = userService.findById(seller.getId()).getUserName();
        return BuyerMapper.toSellerDto(seller, buyerUserName);
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

    @Override
    public Seller findSellerById(UUID sellerId) {
        return getSellerById(sellerId);
    }

    @Override
    public SellerAveragePrice findPricePerPosts(UUID userId) {
        Double averagePrice = postService.findAveragePrice(userId);
        return new SellerAveragePrice(
                userId,
                userService.findById(userId).getUserName(),
                averagePrice);
    }

    @Override
    public SellerDto sortFollowersByName(UUID sellerId, String order) {
        Seller seller = getExistingSeller(sellerId);

        List<Buyer> followers = new ArrayList<>(seller.getFollowers());

        Comparator<Buyer> comparator = getComparatorForOrder(order);
        followers.sort(comparator);

        seller.setFollowers(followers);

        return mapToSellerDto(seller);
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

    private boolean validateMutualFollowing(Buyer buyer, Seller seller) {
        boolean isFollowedBy = sellerRepository.sellerIsBeingFollowedByBuyer(buyer, seller.getId());
        boolean isFollowing = buyerService.buyerIsFollowingSeller(seller, buyer.getId());
        return isFollowing && isFollowedBy;
    }

    private Seller getExistingSeller(UUID sellerId) {
        return sellerRepository.findSellerById(sellerId)
                .orElseThrow(() -> new BadRequest("No seller found for " + sellerId));
    }

    private SellerDto mapToSellerDto(Seller seller) {
        Map<UUID, String> userNames = new HashMap<>();
        userNames.computeIfAbsent(seller.getId(), id -> userService.findById(id).getUserName());
        String sellerUsername = userNames.get(seller.getId());

        List<BuyerResponseDto> followers = seller.getFollowers().stream()
                .map(buyer -> {
                    userNames.computeIfAbsent(buyer.getId(), id -> userService.findById(id).getUserName());
                    String userName = userNames.get(buyer.getId());
                    return new BuyerResponseDto(buyer.getId(), userName, new ArrayList<>());
                })
                .toList();

        return new SellerDto(
                seller.getId(),
                sellerUsername,
                followers,
                seller.getFollowerCount());
    }

    private Comparator<Buyer> getComparatorForOrder(String order) {
        Comparator<Buyer> comparator = Comparator.comparing(
                buyer -> userService.findById(buyer.getId()).getUserName());

        if ("name_desc".equalsIgnoreCase(order)) {
            return comparator.reversed();
        } else if ("name_asc".equalsIgnoreCase(order)) {
            return comparator;
        } else {
            throw new BadRequest("Invalid sorting parameter: " + order
                    + ", please try again with a valid one (name_asc or name_desc)");
        }
    }

}