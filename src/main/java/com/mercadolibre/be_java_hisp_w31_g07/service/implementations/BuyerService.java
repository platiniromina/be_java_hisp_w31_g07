package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IBuyerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IBuyerService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IUserService;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuyerService implements IBuyerService {
    private final IBuyerRepository buyerRepository;
    private final IUserService userService;

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
    public BuyerDto findFollowed(UUID buyerId) {
        Buyer buyer = this.findBuyerById(buyerId);
        return mapToDto(buyer);
    }

    private BuyerDto mapToDto(Buyer buyer) {
        List<SellerResponseDto> followedSellers = buyer.getFollowed().stream()
                .map(seller -> {
                    SellerResponseDto sellerResponseDto = new SellerResponseDto();
                    sellerResponseDto.setId(seller.getId());
                    sellerResponseDto.setUserName(userService.findById(seller.getId()).getUserName());
                    return sellerResponseDto;
                })
                .toList();

        return new BuyerDto(
                buyer.getId(),
                userService.findById(buyer.getId()).getUserName(),
                followedSellers);
    }

    @Override
    public void removeSellerFromFollowedList(Seller seller, UUID buyerId) {
        buyerRepository.removeSellerFromFollowedList(seller, buyerId);
    }

}
