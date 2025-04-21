package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.User;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IBuyerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IBuyerService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuyerService implements IBuyerService {
    private final IBuyerRepository buyerRepository;
    private final IUserService userService;

    @Override
    public BuyerDto findFollowed(UUID userId){
        Buyer buyer = buyerRepository.findFollowed(userId)
                .orElseThrow(()-> new NotFoundException("No se encontro al usuario: " + userId));

        return mapToDto(buyer);
    }

    private BuyerDto mapToDto(Buyer buyer){
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
                followedSellers
        );
    }
}
