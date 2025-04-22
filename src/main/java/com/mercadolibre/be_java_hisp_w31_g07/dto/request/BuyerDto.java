package com.mercadolibre.be_java_hisp_w31_g07.dto.request;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BuyerDto {
    private UUID id;
    private String userName;
    @JsonManagedReference
    private List<SellerResponseDto> followed;
}
