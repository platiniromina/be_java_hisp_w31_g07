package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;

import java.util.Map;
import java.util.UUID;

public interface IUserService {
    /**
     * Returns a user that match with the param id.
     *
     * @param id id to find a user.
     * @return a user dto if the user is found,
     * or an exception.
     */
    UserDto findById(UUID id);

    /**
     * Retrieves a map of user IDs to their usernames based on the provided ID map.
     * <p>
     * This method takes a map of user IDs and returns a new map where the keys are
     * the user IDs and the values are the corresponding usernames. If the input map
     * is null or empty, an empty map is returned.
     *
     * @param idMap a map containing user IDs as keys
     * @return a map of user IDs to usernames, or an empty map if the input is null or empty
     */
    Map<UUID, String> findUsernames(Map<UUID, String> idMap);
}
