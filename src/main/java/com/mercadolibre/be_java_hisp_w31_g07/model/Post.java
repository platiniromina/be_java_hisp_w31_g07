package com.mercadolibre.be_java_hisp_w31_g07.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
public class Post {
    private UUID id;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private UUID productId;
    private String category;
    private Double price;
    private UUID sellerId;
    private Boolean hasPromo;
    private Integer discount;
}
