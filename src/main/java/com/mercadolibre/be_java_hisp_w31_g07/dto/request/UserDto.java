package com.mercadolibre.be_java_hisp_w31_g07.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @JsonProperty("user_id")
    private UUID id;

    @JsonProperty("user_name")
    @Length(max = 15, message = "UserName cannot be more than 15 characters")
    @NotBlank(message = "UserName cannot be blank")
    private String userName;

    @JsonProperty("first_name")
    @Pattern(regexp = "([A-Z]|[0-9])[\\s|[0-9]|A-Z|a-z|ñ|ó|í|á|é|ú|Á|Ó|É|Í|Ú]*$",
            message = "The name must be in the correct format")
    private String firstName;

    @JsonProperty("last_name")
    @Pattern(regexp = "([A-Z]|[0-9])[\\s|[0-9]|A-Z|a-z|ñ|ó|í|á|é|ú|Á|Ó|É|Í|Ú]*$",
            message = "The last name must be in the correct format")
    private String lastName;

    @Email(message = "Email format must be: \"hola@example.com\"")
    private String email;
}
