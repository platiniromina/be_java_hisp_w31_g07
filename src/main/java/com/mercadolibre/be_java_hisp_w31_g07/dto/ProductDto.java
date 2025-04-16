package com.mercadolibre.be_java_hisp_w31_g07.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Integer id;
    private String productName;
    private String type;
    private String brand;
    private String color;
    private String note;
}
