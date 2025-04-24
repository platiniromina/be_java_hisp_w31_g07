package com.mercadolibre.be_java_hisp_w31_g07.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BuyerPurchasesResponseDto {
    @JsonProperty("user_id")
    private UUID buyerId;
    @JsonProperty("user_name")
    private String userName;
    private PostDto purchases;
}
