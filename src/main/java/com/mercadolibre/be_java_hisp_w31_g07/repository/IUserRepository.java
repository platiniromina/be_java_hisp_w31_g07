package com.mercadolibre.be_java_hisp_w31_g07.repository;

import com.mercadolibre.be_java_hisp_w31_g07.model.User;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository {
    /**
     * Returns a user that match with the param userId.
     *
     * @param userId id to find a user.
     * @return a user with his attributes if the user is found,
     * or an empty Optional otherwise.
     */
    public Optional<User> findById(UUID userId);

}
