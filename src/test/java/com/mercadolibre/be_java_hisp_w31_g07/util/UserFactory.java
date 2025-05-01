package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;

import java.util.UUID;

public class UserFactory {

    public static UserDto createUserDto() {
        UserDto user = new UserDto();
        user.setId(UUID.randomUUID());
        user.setUserName("testUser");
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        return user;
    }
}
