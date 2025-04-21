package com.mercadolibre.be_java_hisp_w31_g07.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.SellerDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
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
public class BuyerReponseDto {
    private UUID id;
    private String userName;
    @JsonIgnore
    private List<SellerResponseDto> followed;
}
