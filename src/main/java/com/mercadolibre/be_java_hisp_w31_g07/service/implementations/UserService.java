package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.User;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IUserRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepository;

    /**
     * Finds a user by their unique identifier.
     *
     * @param userId the unique identifier of the user to be retrieved
     * @return the User object corresponding to the given userId
     * @throws BadRequest if no user is found with the specified userId
     */
    @Override
    public User findUserById(UUID userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new BadRequest("User with id " + userId + " not found"));
    }
}
