package com.mercadolibre.be_java_hisp_w31_g07.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    private Product product;
    private Integer category;
    private Double price;
    private UUID sellerId;
    private Boolean hasPromo;
    private Double discount;

    public void setGeneratedId(UUID id) {
        this.id = id;
        product.setId(id);
    }
}
