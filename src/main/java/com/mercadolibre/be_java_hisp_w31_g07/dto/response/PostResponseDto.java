package com.mercadolibre.be_java_hisp_w31_g07.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PostResponseDto {
    private UUID id;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    private ProductResponseDto product;
    private Integer category;
    private Double price;
    @JsonProperty("user_id")
    private UUID sellerId;
    @JsonProperty("has_promo")
    private Boolean hasPromo;
    private Double discount;
}
