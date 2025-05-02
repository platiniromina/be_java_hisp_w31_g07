package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.User;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IUserRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IUserService;
import com.mercadolibre.be_java_hisp_w31_g07.util.ErrorMessagesUtil;
import com.mercadolibre.be_java_hisp_w31_g07.util.GenericObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final GenericObjectMapper mapper;

    @Override
    public UserDto findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequest(ErrorMessagesUtil.userNotFound(id)));
        return mapper.map(user, UserDto.class);
    }

    @Override
    public Map<UUID, String> findUsernames(Map<UUID, String> idMap) {
        if (idMap == null || idMap.isEmpty()) return Collections.emptyMap();

        List<User> users = userRepository.findAllByIdIn(idMap.keySet());

        return users.stream()
                .collect(Collectors.toMap(User::getId, User::getUserName));
    }

}
