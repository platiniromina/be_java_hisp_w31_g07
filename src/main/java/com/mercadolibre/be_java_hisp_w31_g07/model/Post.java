package com.mercadolibre.be_java_hisp_w31_g07.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private UUID id;
    private LocalDate date;
    private UUID productId;
    private String category;
    private Double price;
    private UUID sellerId;
    private Boolean hasPromo;
    private Integer discount;
}
