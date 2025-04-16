package com.mercadolibre.be_java_hisp_w31_g07.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.mercadolibre.be_java_hisp_w31_g07.service.IUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @GetMapping("/users/{userId}/follow/{userIdToFollow}")
    public ResponseEntity<?> followUser(
            @PathVariable UUID userId,
            @PathVariable UUID userIdToFollow) {
        userService.followUser(userId, userIdToFollow);
        return ResponseEntity.ok("User followed successfully");
    }

}
