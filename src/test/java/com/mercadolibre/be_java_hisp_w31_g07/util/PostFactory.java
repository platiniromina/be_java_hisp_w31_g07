package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.PostDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.ProductDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.ProductResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.model.Product;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class PostFactory {

    private static final String FIXED_DATE = "30-04-2025";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final LocalDate DEFAULT_DATE = LocalDate.parse(FIXED_DATE, DATE_FORMATTER);
    private static final int DEFAULT_CATEGORY = 1;
    private static final double DEFAULT_PRICE = 100.0;
    private static final double DEFAULT_DISCOUNT = 0.1;

    private static final String DEFAULT_PRODUCT_NAME = "Test product";
    private static final String DEFAULT_TYPE = "Test type";
    private static final String DEFAULT_BRAND = "Test Brand";
    private static final String DEFAULT_COLOR = "Test Color";
    private static final String DEFAULT_NOTE = "Test Note";

    public static Post createPost(UUID sellerId, boolean hasPromo) {
        UUID postId = UUID.randomUUID();
        Post post = new Post();
        post.setId(postId);
        post.setProduct(createProduct(postId));
        populatePostFields(post, sellerId, hasPromo);
        return post;
    }

    public static PostDto createPostDto(UUID sellerId, boolean hasPromo) {
        PostDto dto = new PostDto();
        dto.setProduct(createProductDto());
        populatePostFields(dto, sellerId, hasPromo);
        return dto;
    }

    public static PostResponseDto createPostResponseDto(UUID sellerId, UUID postId, boolean hasPromo) {
        PostResponseDto dto = new PostResponseDto();
        dto.setId(postId);
        dto.setProduct(createProductResponseDto(postId));
        populatePostFields(dto, sellerId, hasPromo);
        return dto;
    }

    public static Product createProduct(UUID productId) {
        Product product = new Product();
        populateProductFields(product, productId);
        return product;
    }

    public static ProductDto createProductDto() {
        ProductDto productDto = new ProductDto();
        populateProductFields(productDto, null);
        return productDto;
    }

    public static ProductResponseDto createProductResponseDto(UUID productId) {
        ProductResponseDto dto = new ProductResponseDto();
        populateProductFields(dto, productId);
        return dto;
    }

    // --- Populate Methods ---

    private static void populatePostFields(Post post, UUID sellerId, boolean hasPromo) {
        post.setDate(DEFAULT_DATE);
        post.setCategory(DEFAULT_CATEGORY);
        post.setPrice(DEFAULT_PRICE);
        post.setSellerId(sellerId);
        post.setHasPromo(hasPromo);
        post.setDiscount(hasPromo ? DEFAULT_DISCOUNT : 0.0);
    }

    private static void populatePostFields(PostDto dto, UUID sellerId, boolean hasPromo) {
        dto.setDate(DEFAULT_DATE);
        dto.setCategory(DEFAULT_CATEGORY);
        dto.setPrice(DEFAULT_PRICE);
        dto.setSellerId(sellerId);
        dto.setHasPromo(hasPromo);
        dto.setDiscount(hasPromo ? DEFAULT_DISCOUNT : 0.0);
    }

    private static void populatePostFields(PostResponseDto dto, UUID sellerId, boolean hasPromo) {
        dto.setDate(DEFAULT_DATE);
        dto.setCategory(DEFAULT_CATEGORY);
        dto.setPrice(DEFAULT_PRICE);
        dto.setSellerId(sellerId);
        dto.setHasPromo(hasPromo);
        dto.setDiscount(hasPromo ? DEFAULT_DISCOUNT : 0.0);
    }

    private static void populateProductFields(Object target, UUID id) {
        if (target instanceof Product product) {
            product.setId(id);
            product.setProductName(DEFAULT_PRODUCT_NAME);
            product.setType(DEFAULT_TYPE);
            product.setBrand(DEFAULT_BRAND);
            product.setColor(DEFAULT_COLOR);
            product.setNote(DEFAULT_NOTE);
        } else if (target instanceof ProductResponseDto dto) {
            dto.setId(id);
            dto.setProductName(DEFAULT_PRODUCT_NAME);
            dto.setType(DEFAULT_TYPE);
            dto.setBrand(DEFAULT_BRAND);
            dto.setColor(DEFAULT_COLOR);
            dto.setNote(DEFAULT_NOTE);
        } else if (target instanceof ProductDto productDto) {
            productDto.setProductName(DEFAULT_PRODUCT_NAME);
            productDto.setType(DEFAULT_TYPE);
            productDto.setBrand(DEFAULT_BRAND);
            productDto.setColor(DEFAULT_COLOR);
            productDto.setNote(DEFAULT_NOTE);
        }
    }
}
