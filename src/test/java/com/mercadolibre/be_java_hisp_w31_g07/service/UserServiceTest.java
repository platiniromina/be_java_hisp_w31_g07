package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.User;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IUserRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.implementations.UserService;
import com.mercadolibre.be_java_hisp_w31_g07.util.ErrorMessagesUtil;
import com.mercadolibre.be_java_hisp_w31_g07.util.GenericObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.util.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    GenericObjectMapper mapper;
    @Mock
    IUserRepository userRepository;
    @InjectMocks
    private UserService userService;

    private User user;
    private UUID userId;
    private UserDto userDto;


    @BeforeEach
    void setUp() {
        user = UserFactory.createUser();
        userId = user.getId();
        userDto = UserFactory.createUserDto(userId);
    }

    @Test
    @DisplayName("[SUCCESS] Find user by ID")
    void testFindUserByIdSuccess() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.findById(userId);

        assertEquals(userDto, result);
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("[ERROR] Find user by ID - Not Found")
    void testFindUserByIdNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        BadRequest exception = assertThrows(BadRequest.class, () -> userService.findById(userId));

        assertEquals(ErrorMessagesUtil.userNotFound(userId), exception.getMessage());
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("[SUCCESS] Find userNames")
    void testFindUsernamesSuccess() {
        Map<UUID, String> idMap = new HashMap<>();
        idMap.put(userId, userDto.getUserName());
        when(userRepository.findAllById(idMap.keySet())).thenReturn(Collections.singletonList(user));

        Map<UUID, String> result = userService.findUsernames(idMap);

        assertEquals(1, result.size());
        assertEquals(userDto.getUserName(), result.get(userId));
        verify(userRepository).findAllById(idMap.keySet());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("[SUCCESS] Find userNames - Empty Map")
    void testFindUsernamesEmptyMap() {
        Map<UUID, String> idMap = new HashMap<>();

        Map<UUID, String> result = userService.findUsernames(idMap);

        assertEquals(Collections.emptyMap(), result);
        verifyNoInteractions(userRepository);
    }

}