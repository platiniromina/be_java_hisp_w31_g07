package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.UserPostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.User;

import java.util.UUID;

public class UserFactory {

    private static final String DEFAULT_USERNAME = "testUser";
    private static final String DEFAULT_EMAIL = "test@test.com";
    private static final String DEFAULT_FIRST_NAME = "Test";
    private static final String DEFAULT_LAST_NAME = "User";

    public static UserDto createUserDto(UUID id) {
        UserDto userDto = new UserDto();
        userDto.setId(id != null ? id : UUID.randomUUID());
        populateUserFields(userDto);
        return userDto;
    }

    public static User createUser(UUID id) {
        User user = new User();
        user.setId(id != null ? id : UUID.randomUUID());
        populateUserFields(user);
        return user;
    }

    public static UserPostResponseDto createUserPostResponseDto(UUID id) {
        UserPostResponseDto userPostResponseDto = new UserPostResponseDto();
        userPostResponseDto.setUserId(id != null ? id : UUID.randomUUID());
        populateUserFields(userPostResponseDto);
        return userPostResponseDto;
    }

    // --- Populate Method ---

    private static void populateUserFields(Object target) {
        if (target instanceof User user) {
            user.setUserName(DEFAULT_USERNAME);
            user.setEmail(DEFAULT_EMAIL);
            user.setFirstName(DEFAULT_FIRST_NAME);
            user.setLastName(DEFAULT_LAST_NAME);
        } else if (target instanceof UserDto userDto) {
            userDto.setUserName(DEFAULT_USERNAME);
            userDto.setEmail(DEFAULT_EMAIL);
            userDto.setFirstName(DEFAULT_FIRST_NAME);
            userDto.setLastName(DEFAULT_LAST_NAME);
        } else if (target instanceof UserPostResponseDto userPostResponseDto) {
            userPostResponseDto.setUserName(DEFAULT_USERNAME);
        }
    }
}
