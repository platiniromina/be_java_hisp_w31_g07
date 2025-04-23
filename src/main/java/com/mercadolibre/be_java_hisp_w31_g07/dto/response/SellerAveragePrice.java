package com.mercadolibre.be_java_hisp_w31_g07.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SellerAveragePrice {
    @JsonProperty("user_id")
    private UUID idSeller;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("average_price")
    private Double averagePrice;
}
