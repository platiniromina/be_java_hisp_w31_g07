package com.mercadolibre.be_java_hisp_w31_g07.dto.response;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BuyerPurchasesResponseDto {
    private UUID buyerId;
    private String userName;
    private PostDto purchases;

}
