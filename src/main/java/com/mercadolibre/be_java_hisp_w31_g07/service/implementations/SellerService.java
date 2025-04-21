package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerFollowersCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.ISellerService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerService implements ISellerService {
    private final ISellerRepository sellerRepository;
    private final IUserService userService;

    @Override
    public SellerFollowersCountResponseDto findFollowersCount(UUID sellerId) {
        Integer count = sellerRepository.findFollowersCount(sellerId)
                .orElseThrow(() -> new BadRequest("User with id " + sellerId + " not found"));

        String userName = userService.findUserById(sellerId).getUserName();

        return new SellerFollowersCountResponseDto(sellerId, userName, count);
    }
}
