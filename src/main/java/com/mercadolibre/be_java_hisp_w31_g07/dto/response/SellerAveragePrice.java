package com.mercadolibre.be_java_hisp_w31_g07.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SellerAveragePrice {
    private UUID idSeller;
    private String userName;
    private Double averagePrice;
}
