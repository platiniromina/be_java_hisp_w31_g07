package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.User;

import java.util.UUID;

public class UserFactory {

    public static User createUser(UUID userId) {
        User user = new User();
        user.setId(userId != null ? userId : UUID.randomUUID());
        user.setUserName("testUser");
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        return user;
    }

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
