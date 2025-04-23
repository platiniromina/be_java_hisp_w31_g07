package com.mercadolibre.be_java_hisp_w31_g07.dto.response;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("user_id")
    private UUID id;
    @JsonProperty("user_name")
    private String userName;
    @JsonIgnore
    @JsonProperty("follower_count")
    private Integer followerCount;
    @JsonBackReference
    private List<BuyerResponseDto> followers;
}
