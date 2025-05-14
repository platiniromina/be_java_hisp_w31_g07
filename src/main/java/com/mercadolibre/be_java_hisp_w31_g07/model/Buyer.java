package com.mercadolibre.be_java_hisp_w31_g07.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Buyer {
    @JsonProperty("user_id")
    private UUID id;
    private List<Seller> followed = new ArrayList<>();

    public void addFollowedSeller(Seller seller) {
        this.followed.add(seller);
    }

    public void removeFollowedSeller(Seller seller) {
        this.followed.remove(seller);
    }

}
