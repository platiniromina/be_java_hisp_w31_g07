package com.mercadolibre.be_java_hisp_w31_g07.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Seller{
    private Integer id;
    private List<Buyer> followers;
    private String billingAddress;
    private String cuil;
    private Integer followerCount;
}
