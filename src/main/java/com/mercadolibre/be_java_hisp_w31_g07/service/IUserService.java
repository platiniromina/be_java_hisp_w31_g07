package com.mercadolibre.be_java_hisp_w31_g07.service;

import java.util.UUID;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;

public interface IUserService {
    /**
     * Returns a user that match with the param id.
     *
     * @param id id to find a user.
     * @return a user dto if the user is found,
     *         or an exception.
     */
    public UserDto findById(UUID id);
}
