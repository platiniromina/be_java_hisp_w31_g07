package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IBuyerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IBuyerService;
import com.mercadolibre.be_java_hisp_w31_g07.util.BuyerMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuyerService implements IBuyerService {
        private final IBuyerRepository buyerRepository;

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
                Buyer buyer = buyerRepository.findFollowed(userId)
                                .orElseThrow(() -> new NotFoundException("Buyer: " + userId + " not found"));

                return BuyerMapper.toBuyerDto(buyer);
        }

        @Override
        public void removeSellerFromFollowedList(Seller seller, UUID buyerId) {
                buyerRepository.removeSellerFromFollowedList(seller, buyerId);
        }
}
