package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;

import java.util.List;
import java.util.UUID;

public interface IPostService {
    public List<PostResponseDto> getLatestPostsFromSellers(UUID buyerId);
}
