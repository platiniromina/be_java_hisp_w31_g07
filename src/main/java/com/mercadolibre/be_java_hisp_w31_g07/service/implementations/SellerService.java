package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.SellerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.BuyerReponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.model.User;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.ISellerService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerService implements ISellerService {
    private final ISellerRepository sellerRepository;
    private final IUserService userService;

    @Override
    public SellerDto findFollowers(UUID userId){
        Seller seller = sellerRepository.findFollowers(userId)
                .orElseThrow(()-> new NotFoundException("No se encontro el usuario"));

        return mapToDto(seller);
    }

    private SellerDto mapToDto(Seller seller){
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
                seller.getFollowerCount()
        );
    }
}
