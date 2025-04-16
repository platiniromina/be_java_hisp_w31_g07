package com.mercadolibre.be_java_hisp_w31_g07.dto.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private UUID id;
    private String date;
    private UUID productId;
    private String category;
    private Double price;
    private UUID sellerId;
    private Boolean hasPromo;
    private Integer discount;
}
