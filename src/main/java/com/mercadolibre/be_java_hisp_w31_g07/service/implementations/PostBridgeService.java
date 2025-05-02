package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.model.Product;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostBridgeService implements IPostBridgeService {

    private final IUserService userService;
    private final ISellerService sellerService;
    private final IBuyerService buyerService;
    private final IProductService productService;

    @Override
    public String getUserName(UUID userId) {
        return userService.findById(userId).getUserName();
    }

    @Override
    public void createProduct(Product product) {
        productService.createProduct(product);
    }

    @Override
    public UserDto getUserById(UUID userId) {
        return userService.findById(userId);
    }

    @Override
    public List<Seller> getFollowed(UUID buyerId) {
        return buyerService.findBuyerById(buyerId).getFollowed();
    }

    @Override
    public void validateSellerExists(UUID sellerId) {
        sellerService.findSellerById(sellerId);
    }

}