package com.mercadolibre.be_java_hisp_w31_g07.controller;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor

@RequestMapping("/products")
public class PostController {
    private final IPostService postService;

    @PostMapping("/post")
    public ResponseEntity<String> createPost(@RequestBody PostDto newPost) {
        UUID post = postService.createPost(newPost);
        return new ResponseEntity<>("Post " + post + " succesfully created", HttpStatus.OK);
    }

    // for testing
    @GetMapping("/post/{postId}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable UUID postId) {
        return new ResponseEntity<>(postService.findPost(postId), HttpStatus.FOUND);
    }
}
