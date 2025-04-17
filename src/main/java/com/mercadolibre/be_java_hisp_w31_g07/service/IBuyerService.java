package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;

import java.util.UUID;

public interface IBuyerService {
    BuyerDto findFollowed(UUID userId);
}
