package com.mercadolibre.be_java_hisp_w31_g07.model;

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
public class Seller {
    @JsonProperty("user_id")
    private UUID id;
    @JsonProperty("follower_count")
    private Integer followerCount;
    private List<Buyer> followers;

    public void addFollower(Buyer buyer) {
        this.followers.add(buyer);
    }

    public void incrementFollowerCount() {
        this.followerCount++;
    }

    public void removeFollower(Buyer buyer) {
        this.followers.remove(buyer);
    }

    public void decrementFollowerCount() {
        this.followerCount--;
    }
}
