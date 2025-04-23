package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.UserPostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Product;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IProductRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IPostService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IProductService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final IProductRepository productRepository;
    private final IUserService userService;

    @Autowired
    @Lazy
    private IPostService postService;

    @Override
    public void createProduct(Product product) {
        productRepository.createProduct(product);
    }

    @Override
    public UserPostResponseDto getSellerPromoPosts(UUID userId) {
        List<PostResponseDto> postResponseDtos = postService.findUserPromoPosts(userId);

        return new UserPostResponseDto(
                userService.findById(userId).getId(),
                userService.findById(userId).getUserName(),
                postResponseDtos);
    }
}
