package com.mercadolibre.be_java_hisp_w31_g07.repository;

import com.mercadolibre.be_java_hisp_w31_g07.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IUserRepository {
    /**
     * Returns a user that match with the param userId.
     *
     * @param userId id to find a user.
     * @return a user with his attributes if the user is found,
     * or an empty Optional otherwise.
     */
    Optional<User> findById(UUID userId);

    /**
     * Retrieves a list of users whose IDs match the given set of UUIDs.
     *
     * @param ids a set of UUIDs to search for in the user repository.
     * @return a list of {@link User} objects that match the provided IDs.
     * If no users are found, an empty list is returned.
     */
    List<User> findAllById(Set<UUID> ids);

    /**
     * Saves a user in the repository.
     *
     * @param user the user to save.
     */
    void save(User user);

}
