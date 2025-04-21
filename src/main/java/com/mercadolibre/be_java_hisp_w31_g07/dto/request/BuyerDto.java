package com.mercadolibre.be_java_hisp_w31_g07.dto.request;

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
    private UUID id;
    private String userName;
    private List<SellerResponseDto> followed;
}
