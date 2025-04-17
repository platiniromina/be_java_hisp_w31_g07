package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;

import java.util.UUID;

public interface ISellerService {
    SellerResponseDto findFollowers(UUID userId);
}
