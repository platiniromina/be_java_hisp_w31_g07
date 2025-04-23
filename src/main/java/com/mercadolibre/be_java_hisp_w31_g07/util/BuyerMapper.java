package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.SellerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.BuyerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class BuyerMapper {

    public static BuyerDto toBuyerDto(Buyer buyer, String userName) {
        BuyerDto buyerDto = new BuyerDto();
        buyerDto.setId(buyer.getId());

        buyerDto.setUserName(userName);

        List<SellerResponseDto> followedSellers = buyer.getFollowed().stream()
                .map(seller -> toSellerResponseDto(seller, userName))
                .collect(Collectors.toList());

        buyerDto.setFollowed(followedSellers);

        return buyerDto;
    }

    public static SellerResponseDto toSellerResponseDto(Seller seller, String userName) {
        if (seller == null) {
            return null;
        }

        SellerResponseDto sellerDto = new SellerResponseDto();
        sellerDto.setId(seller.getId());
        sellerDto.setUserName(userName);

        return sellerDto;
    }

    public static SellerDto toSellerDto(Seller seller, String userName) {
        SellerDto sellerDto = new SellerDto();
        sellerDto.setId(seller.getId());

        sellerDto.setUserName(userName);

        List<BuyerResponseDto> followerBuyer = seller.getFollowers().stream()
                .map(buyer -> toBuyerReponseDto(buyer, userName))
                .collect(Collectors.toList());

        sellerDto.setFollowers(followerBuyer);
        sellerDto.setFollowerCount(followerBuyer.size());

        return sellerDto;
    }

    public static BuyerResponseDto toBuyerReponseDto(Buyer buyer, String userName) {
        if (buyer == null) {
            return null;
        }

        BuyerResponseDto buyerResponseDto = new BuyerResponseDto();
        buyerResponseDto.setId(buyer.getId());
        buyerResponseDto.setUserName(userName);

        return buyerResponseDto;
    }
}
