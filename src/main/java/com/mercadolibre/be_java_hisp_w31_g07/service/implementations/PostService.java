package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IPostRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final IPostRepository postRepository;

    @Override
    public void createPost(PostDto newPost) {
        if (newPost == null) {
            throw new BadRequest("Datos ingresados inválidos");
        }
        ObjectMapper mapper = new ObjectMapper();
        UUID newId = UUID.randomUUID();

        Post post = mapper.convertValue(newPost, Post.class);

        post.setId(newId);
        postRepository.createPost(post);
    }
}
