package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.implementations.BuyerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.implementations.SellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.implementations.UserRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final BuyerRepository buyerRepository;

    @Override
    public void followUser(UUID userId, UUID userIdToFollow) {
        Buyer buyer = buyerRepository.findBuyerById(userId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        Seller seller = sellerRepository.findSellerById(userIdToFollow)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        sellerRepository.addFollower(buyer, userIdToFollow);
        buyerRepository.followSeller(seller, userIdToFollow);
    }
}
