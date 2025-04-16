package com.mercadolibre.be_java_hisp_w31_g07.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Integer id;
    private LocalDate date;
    private Integer productId;
    private String category;
    private Double price;
    private Integer sellerId;
    private Boolean hasPromo;
    private Integer discount;
}
