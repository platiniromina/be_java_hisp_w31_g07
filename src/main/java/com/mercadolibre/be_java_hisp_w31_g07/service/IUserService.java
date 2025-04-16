package com.mercadolibre.be_java_hisp_w31_g07.service;

import java.util.UUID;

public interface IUserService {
    public void followUser(UUID userId, UUID userIdToFollow);
}
