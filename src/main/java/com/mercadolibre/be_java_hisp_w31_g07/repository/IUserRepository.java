package com.mercadolibre.be_java_hisp_w31_g07.repository;

import com.mercadolibre.be_java_hisp_w31_g07.model.User;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository {
    Optional<User> findById(UUID userId);
}
