package com.mercadolibre.be_java_hisp_w31_g07.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private UUID id;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
}
