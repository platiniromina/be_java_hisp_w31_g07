package com.mercadolibre.be_java_hisp_w31_g07.repository;

import com.mercadolibre.be_java_hisp_w31_g07.model.Post;

import java.util.List;
import java.util.UUID;

public interface IPostRepository {
    public List<Post> findRecentPostsBySellerId(UUID sellerId);
}
