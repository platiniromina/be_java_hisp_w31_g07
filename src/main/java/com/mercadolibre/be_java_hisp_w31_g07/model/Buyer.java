package com.mercadolibre.be_java_hisp_w31_g07.model;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Buyer {
    private UUID id;
    private List<Seller> followed;

    public void addFollowedSeller(Seller seller) {
        this.followed.add(seller);
    }

    public void removeFollowedSeller(Seller seller) {
        this.followed.remove(seller);
    }

}
