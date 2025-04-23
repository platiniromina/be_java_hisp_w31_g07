package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import java.util.UUID;

import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.User;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IUserRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepository;

    @Override
    public UserDto findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequest(
                        "User not found: " + id));
        return mapToDto(user);
    }

    private UserDto mapToDto(User user) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(user, UserDto.class);
    }

}
