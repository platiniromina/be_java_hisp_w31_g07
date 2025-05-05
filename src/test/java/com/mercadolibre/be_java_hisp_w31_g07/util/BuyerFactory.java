package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.BuyerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;

import java.util.ArrayList;
import java.util.UUID;

public class BuyerFactory {

    private static final String DEFAULT_USER_NAME = "testUser";

    public static Buyer createBuyer(UUID id) {
        Buyer buyer = new Buyer();
        buyer.setId(id != null ? id : UUID.randomUUID());
        buyer.setFollowed(new ArrayList<>());
        return buyer;
    }

    public static BuyerDto createBuyerDto() {
        BuyerDto buyerDto = new BuyerDto();
        buyerDto.setId(UUID.randomUUID());
        buyerDto.setFollowed(new ArrayList<>());
        return buyerDto;
    }

    public static BuyerResponseDto createBuyerResponseDto(UUID buyerId) {
        BuyerResponseDto buyerRespDto = new BuyerResponseDto();
        buyerRespDto.setId(buyerId);
        buyerRespDto.setUserName(DEFAULT_USER_NAME);
        buyerRespDto.setFollowed(new ArrayList<>());
        return buyerRespDto;
    }

}

