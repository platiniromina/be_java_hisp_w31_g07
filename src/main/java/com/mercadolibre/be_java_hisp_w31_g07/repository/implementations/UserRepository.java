package com.mercadolibre.be_java_hisp_w31_g07.repository.implementations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.model.User;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IUserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository implements IUserRepository {
    private List<User> userList = new ArrayList<>();

    public UserRepository() throws IOException {
        loadDataBaseUsers();
    }

    private void loadDataBaseUsers() throws IOException {
        File file;
        ObjectMapper objectMapper = new ObjectMapper();
        List<User> users;

        file = ResourceUtils.getFile("classpath:user.json");
        users = objectMapper.readValue(file, new TypeReference<List<User>>() {
        });

        userList = users;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return userList.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();
    }

}
