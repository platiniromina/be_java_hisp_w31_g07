package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.ProductResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.model.Product;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class PostFactory {

    public static Post createPost(UUID sellerId) {
        UUID postId = UUID.randomUUID();
        Post post = new Post();
        post.setId(postId);
        post.setDate(LocalDate.parse("30-04-2025", DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        post.setProduct(createProduct(postId));
        post.setCategory(1);
        post.setPrice(100.0);
        post.setSellerId(sellerId);
        post.setHasPromo(false);
        post.setDiscount(0.0);
        return post;
    }

    public static PostResponseDto createPostResponseDto(UUID postId, UUID sellerId) {
        PostResponseDto postResponseDto = new PostResponseDto();
        postResponseDto.setId(postId);
        postResponseDto.setDate(LocalDate.parse("30-04-2025", DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        postResponseDto.setCategory(1);
        postResponseDto.setPrice(100.0);
        postResponseDto.setHasPromo(false);
        postResponseDto.setDiscount(0.0);
        postResponseDto.setSellerId(sellerId);
        postResponseDto.setProduct(createProductResponseDto(postId));
        return postResponseDto;
    }

    private static Product createProduct(UUID productId) {
        Product product = new Product();
        product.setId(productId);
        product.setProductName("Test product");
        product.setType("Test type");
        product.setBrand("Test Brand");
        product.setColor("Test Color");
        product.setNote("Test Note");
        return product;
    }

    public static ProductResponseDto createProductResponseDto(UUID productId) {
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setId(productId);
        productResponseDto.setProductName("Test product");
        productResponseDto.setType("Test type");
        productResponseDto.setBrand("Test Brand");
        productResponseDto.setColor("Test Color");
        productResponseDto.setNote("Test Note");
        return productResponseDto;
    }
}