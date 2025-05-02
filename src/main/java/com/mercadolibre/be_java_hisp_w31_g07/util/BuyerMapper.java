package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.SellerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.BuyerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class BuyerMapper {

    public static BuyerDto toBuyerDto(Buyer buyer, String userName) {
        BuyerDto buyerDto = new BuyerDto();
        buyerDto.setId(buyer.getId());

        buyerDto.setUserName(userName);

        List<SellerResponseDto> followedSellers = buyer.getFollowed().stream()
                .map(seller -> toSellerResponseDto(seller, userName))
                .toList();

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

    public static SellerDto toSellerDto(Seller seller, String userName, Map<UUID, String> userNames) {
        SellerDto sellerDto = new SellerDto();
        sellerDto.setId(seller.getId());

        sellerDto.setUserName(userName);

        List<BuyerResponseDto> followers = seller.getFollowers().stream()
                .map(buyer -> new BuyerResponseDto(
                        buyer.getId(),
                        userNames.get(buyer.getId()),
                        new ArrayList<>()
                ))
                .toList();

        sellerDto.setFollowers(followers);
        sellerDto.setFollowerCount(followers.size());

        return sellerDto;
    }
    
}
