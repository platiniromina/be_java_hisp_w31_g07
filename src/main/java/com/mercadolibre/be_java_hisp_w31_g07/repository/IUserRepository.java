package com.mercadolibre.be_java_hisp_w31_g07.repository;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository {
    /**
     * Finds the username of a user by their unique identifier (UUID).
     *
     * @param userId the unique identifier of the user to search for
     * @return an Optional containing the username if a user with the given ID
     *         exists,
     *         or an empty Optional otherwise.
     */
    public Optional<String> findUserNameById(UUID userId);
}
