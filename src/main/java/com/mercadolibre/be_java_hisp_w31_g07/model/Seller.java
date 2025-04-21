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
public class Seller {
    private UUID id;
    private String billingAddress;
    private String cuil;
    private Integer followerCount;
    private List<Buyer> followers;

    public void addFollower(Buyer buyer) {
        this.followers.add(buyer);
    }

    public void incrementFollowerCount() {
        this.followerCount++;
    }
}
