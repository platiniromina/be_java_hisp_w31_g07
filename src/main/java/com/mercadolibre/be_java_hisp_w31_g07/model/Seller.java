package com.mercadolibre.be_java_hisp_w31_g07.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Seller {
    private Integer sellerId;
    private Integer followers;
    private String billing_address;
    private String cuil;
    private String trade_name;
    private Integer followerCount;
}
