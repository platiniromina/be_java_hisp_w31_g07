package com.mercadolibre.be_java_hisp_w31_g07.service;

import java.util.UUID;

import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.User;

public interface IUserService {
    /**
     * Finds a user by their unique identifier.
     *
     * @param userId the unique identifier of the user to be retrieved
     * @return the User object corresponding to the given userId
     * @throws BadRequest if no user is found with the specified userId
     */
    public User findUserById(UUID userId);
}
