package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.SellerDto;
import java.util.UUID;

public interface ISellerService {
    SellerDto findFollowers(UUID userId);
}
