package com.mercadolibre.be_java_hisp_w31_g07.dto.response;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FollowersPostsResponseDto {
    private UUID id;
    private List<PostResponseDto> posts;
}
