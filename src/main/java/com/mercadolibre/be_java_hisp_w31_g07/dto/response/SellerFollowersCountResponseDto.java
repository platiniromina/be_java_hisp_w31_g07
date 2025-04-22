package com.mercadolibre.be_java_hisp_w31_g07.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SellerFollowersCountResponseDto {
    private UUID user_id;
    private String user_name;
    private Integer followersCount;
}
