package com.mercadolibre.be_java_hisp_w31_g07.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class SellerResponseDto {
    private UUID id;
    private String billingAddress;
    private String cuil;
    private Integer followerCount;
    @JsonIgnore
    private List<BuyerReponseDto> followers;
}
