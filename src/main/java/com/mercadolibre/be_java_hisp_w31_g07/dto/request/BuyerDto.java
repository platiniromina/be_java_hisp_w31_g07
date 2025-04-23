package com.mercadolibre.be_java_hisp_w31_g07.dto.request;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BuyerDto {
    @JsonProperty("user_id")
    private UUID id;
    @JsonProperty("user_name")
    private String userName;
    @JsonManagedReference
    private List<SellerResponseDto> followed;
}
