package com.mercadolibre.be_java_hisp_w31_g07.repository;

import java.util.Optional;
import java.util.UUID;

import com.mercadolibre.be_java_hisp_w31_g07.model.User;

public interface IUserRepository {
    /**
     * Finds a user by their unique identifier (UUID).
     *
     * @param userId the unique identifier of the user to find
     * @return an {@link Optional} containing the user if found, or an empty
     *         {@link Optional} otherwise.
     */
    public Optional<User> findUserById(UUID userId);
}
