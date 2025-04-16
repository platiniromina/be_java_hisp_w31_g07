package com.mercadolibre.be_java_hisp_w31_g07.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SellerDto {
    private Integer id;
    private String userName;
    private List<BuyerDto> followers;
}
