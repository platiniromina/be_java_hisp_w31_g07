package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.ProductResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.model.Product;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class PostFactory {
    private static final UUID postId = UUID.randomUUID();

    public static Post createPost(UUID sellerId) {
        Post post = new Post();
        post.setId(postId);
        post.setDate(LocalDate.parse("30-04-2025", DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        post.setProduct(createProduct(postId));
        post.setCategory(1);
        post.setPrice(100.0);
        post.setSellerId(sellerId);
        post.setHasPromo(true);
        post.setDiscount(0.1);
        return post;
    }

    public static PostDto createPostDto(UUID sellerId) {
        PostDto postDto = new PostDto();
        postDto.setDate(LocalDate.parse("30-04-2025", DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        postDto.setCategory(1);
        postDto.setPrice(100.0);
        postDto.setSellerId(sellerId);
        postDto.setHasPromo(true);
        postDto.setDiscount(0.1);
        return postDto;
    }

    public static PostResponseDto createPostResponseDto(UUID sellerId) {
        PostResponseDto postResponseDto = new PostResponseDto();
        postResponseDto.setId(postId);
        postResponseDto.setDate(LocalDate.parse("30-04-2025", DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        postResponseDto.setProduct(createProductResponseDto(postId));
        postResponseDto.setCategory(1);
        postResponseDto.setPrice(100.0);
        postResponseDto.setSellerId(sellerId);
        postResponseDto.setHasPromo(true);
        postResponseDto.setDiscount(0.1);
        return postResponseDto;
    }

    public static ProductResponseDto createProductResponseDto(UUID postId) {
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setId(postId);
        productResponseDto.setProductName("Test product");
        productResponseDto.setType("Test type");
        productResponseDto.setBrand("Test Brand");
        productResponseDto.setColor("Test Color");
        productResponseDto.setNote("Test Note");
        return productResponseDto;
    }

    public static Product createProduct(UUID productId) {
        Product product = new Product();
        product.setId(productId);
        product.setProductName("Test product");
        product.setType("Test type");
        product.setBrand("Test Brand");
        product.setColor("Test Color");
        product.setNote("Test Note");
        return product;
    }
}