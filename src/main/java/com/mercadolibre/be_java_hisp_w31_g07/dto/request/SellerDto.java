package com.mercadolibre.be_java_hisp_w31_g07.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.BuyerResponseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SellerDto {
    @JsonProperty("user_id")
    @NotNull(message = "Id cannot be null")
    private UUID id;

    @JsonProperty("user_name")
    @Length(max = 15, message = "UserName cannot be more than 15 characters")
    @NotBlank(message = "UserName cannot be blank")
    private String userName;

    private List<BuyerResponseDto> followers;

    @JsonIgnore
    @JsonProperty("follower_count")
    private Integer followerCount;
}
