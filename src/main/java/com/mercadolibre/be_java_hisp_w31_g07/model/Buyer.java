package com.mercadolibre.be_java_hisp_w31_g07.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Buyer {
    private Integer buyerId;
    private Integer followed;
}
