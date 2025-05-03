package com.mercadolibre.be_java_hisp_w31_g07.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    @JsonFormat(pattern = "dd-MM-yyyy")
    @NotNull(message = "Date cannot be null")
    @PastOrPresent(message = "Date cannot be future")
    private LocalDate date;

    @Valid
    private ProductDto product;

    @NotNull(message = "Category cannot be null")
    @PositiveOrZero
    private Integer category;

    @NotNull(message = "Price cannot be null")
    @Max(value = 10000000, message = "The maximum price is: $10.000.000")
    private Double price;

    @NotNull(message = "Seller ID cannot be null")
    @JsonProperty("user_id")
    private UUID sellerId;

    @JsonProperty("has_promo")
    private Boolean hasPromo;

    @PositiveOrZero
    private Double discount;
}
