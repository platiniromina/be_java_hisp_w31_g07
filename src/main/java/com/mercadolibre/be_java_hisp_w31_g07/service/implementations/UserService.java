package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IBuyerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IUserRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final ISellerRepository sellerRepository;
    private final IBuyerRepository buyerRepository;

    public void followUser(UUID userId, UUID userIdToFollow) {
        Buyer buyer = buyerRepository.findBuyerById(userId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        Seller seller = sellerRepository.findSellerById(userIdToFollow)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        sellerRepository.addFollower(buyer, userIdToFollow);
        buyerRepository.followSeller(seller, userIdToFollow);
    }
}
