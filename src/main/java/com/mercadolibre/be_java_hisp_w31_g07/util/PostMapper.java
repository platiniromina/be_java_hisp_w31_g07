package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final GenericObjectMapper mapper;

    public Post fromPostDtoToPost(PostDto dto) {
        Post post = mapper.map(dto, Post.class);
        post.setGeneratedId(IdUtils.generateId());
        return post;
    }

    public PostDto fromPostToPostDto(Post post) {
        return mapper.map(post, PostDto.class);
    }

    public PostResponseDto fromPostToPostResponseDto(Post post) {
        return mapper.map(post, PostResponseDto.class);
    }

    public List<PostResponseDto> fromPostListToPostResponseDtoList(List<Post> posts) {
        return mapper.mapList(posts, PostResponseDto.class);
    }
}
