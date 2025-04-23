package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.BuyerPurchasesResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IBuyerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IBuyerService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IUserService;
import com.mercadolibre.be_java_hisp_w31_g07.util.BuyerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuyerService implements IBuyerService {
    private final IBuyerRepository buyerRepository;
    private final IUserService userService;
    @Autowired
    @Lazy
    private PostService postService;


    @Override
    public Buyer findBuyerById(UUID id) {
        return buyerRepository.findBuyerById(id)
                .orElseThrow(() -> new BadRequest("Buyer " + id + " not found"));
    }

    @Override
    public void addSellerToFollowedList(Seller seller, UUID buyerId) {
        buyerRepository.addSellerToFollowedList(seller, buyerId)
                .orElseThrow(() -> new BadRequest("Buyer " + buyerId + " not found"));
    }

    @Override
    public boolean buyerIsFollowingSeller(Seller seller, UUID buyerId) {
        return buyerRepository.buyerIsFollowingSeller(seller, buyerId);
    }

    @Override
    public BuyerDto findFollowed(UUID userId) {
        Buyer buyer = buyerRepository.findBuyerById(userId)
                .orElseThrow(() -> new NotFoundException("Buyer: " + userId + " not found"));

        String buyerUserName = userService.findById(buyer.getId()).getUserName();
        return BuyerMapper.toBuyerDto(buyer, buyerUserName);
    }

    @Override
    public void removeSellerFromFollowedList(Seller seller, UUID buyerId) {
        buyerRepository.removeSellerFromFollowedList(seller, buyerId);
    }

    @Override
    public BuyerPurchasesResponseDto findBuyerPurchase(UUID userId, String product) {
        PostDto postDtos = postService.findProductByPurchase(product);
        Buyer buyer = this.findBuyerById(userId);

        return new BuyerPurchasesResponseDto(
                buyer.getId(),
                userService.findById(buyer.getId()).getUserName(),

        )
    }

}
