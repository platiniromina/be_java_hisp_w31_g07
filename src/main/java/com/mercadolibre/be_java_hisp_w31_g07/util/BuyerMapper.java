package com.mercadolibre.be_java_hisp_w31_g07.util;

import java.util.List;
import java.util.stream.Collectors;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;

public class BuyerMapper {

    public static BuyerDto toBuyerDto(Buyer buyer) {
        BuyerDto buyerDto = new BuyerDto();
        buyerDto.setId(buyer.getId());

        buyerDto.setUserName("testbuyer");

        List<SellerResponseDto> followedSellers = buyer.getFollowed().stream()
                .map(BuyerMapper::toSellerResponseDto)
                .collect(Collectors.toList());

        buyerDto.setFollowed(followedSellers);

        return buyerDto;
    }

    public static SellerResponseDto toSellerResponseDto(Seller seller) {
        if (seller == null) {
            return null;
        }

        SellerResponseDto sellerDto = new SellerResponseDto();
        sellerDto.setId(seller.getId());
        sellerDto.setUserName("testseller");

        return sellerDto;
    }

    // // Método que convierte un Buyer a BuyerReponseDto (si es necesario)
    // public static BuyerReponseDto toBuyerReponseDto(Buyer buyer) {
    // if (buyer == null) {
    // return null;
    // }

    // BuyerReponseDto buyerResponseDto = new BuyerReponseDto();
    // buyerResponseDto.setId(buyer.getId());
    // buyerResponseDto.setUserName("test buyer");

    // // No mapeamos el campo "followed" en este DTO porque está marcado con
    // // @JsonIgnore

    // return buyerResponseDto;
    // }
}
