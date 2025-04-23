package com.mercadolibre.be_java_hisp_w31_g07.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SellerPromoPostsCountResponseDto {
    @JsonAlias("user_id")
    private UUID userId;
    @JsonAlias("user_name")
    private String userName;
    @JsonAlias("promo_products_count")
    private Integer promoPostsCount;
}
