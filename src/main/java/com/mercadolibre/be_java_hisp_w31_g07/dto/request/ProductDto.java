package com.mercadolibre.be_java_hisp_w31_g07.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    @JsonProperty("product_name")
    @NotBlank(message = "Product name cannot be blank")
    @Length(max = 40, message = "Product name cannot be more than 40 characters")
    private String productName;

    @NotBlank(message = "The type cannot be blank")
    @Length(max = 15, message = "The type cannot be more than 15 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Special characters are not allowed")
    private String type;

    @Length(max = 25, message = "Brand cannot be more than 25 characters")
    @NotBlank(message = "Brand cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Special characters are not allowed")
    private String brand;

    @Length(max = 15, message = "Color cannot be more than 15 characters")
    @NotBlank(message = "Color cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Special characters are not allowed")
    private String color;

    @Length(max = 80, message = "Notes cannot be more than 80 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Special characters are not allowed")
    private String note;
}
