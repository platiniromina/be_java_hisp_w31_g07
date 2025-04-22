package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import java.util.Map;
import java.util.UUID;
import com.mercadolibre.be_java_hisp_w31_g07.util.GenericObjectMapper;
import org.springframework.stereotype.Service;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IBuyerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IBuyerService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IUserService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuyerService implements IBuyerService {
    private final IBuyerRepository buyerRepository;
    private final IUserService userService;

    @Override
    public Buyer findBuyerById(UUID id) {
        return buyerRepository.findBuyerById(id)
                .orElseThrow(() -> new BadRequest("Buyer " + id + " not found"));
    }

    @Override
    public void addSellerToFollowedList(Seller seller, UUID buyerId) {
        buyerRepository.addSellerToFollowedList(seller, buyerId)
                .orElseThrow(() -> new BadRequest("Buyer " + buyerId + " not found"));
    }

    @Override
    public boolean buyerIsFollowingSeller(Seller seller, UUID buyerId) {
        return buyerRepository.buyerIsFollowingSeller(seller, buyerId);
    }

    @Override
    public BuyerDto findFollowed(UUID userId) {
        Buyer buyer = buyerRepository.findFollowed(userId)
                .orElseThrow(() -> new NotFoundException("Buyer: " + userId + " not found"));

        return mapSellerToDto(buyer);
    }

    @Override
    public void removeSellerFromFollowedList(Seller seller, UUID buyerId) {
        buyerRepository.removeSellerFromFollowedList(seller, buyerId);
    }

    // ------------------------------
    // Util methods to map
    // ------------------------------
    private BuyerDto mapSellerToDto(Buyer buyer){
        ObjectMapper mapper = new ObjectMapper();
        GenericObjectMapper mapToDtos = new GenericObjectMapper(mapper);

        // Obtener userName para Buyer
        Map<String, Object> buyerExtraFields = Map.of(
                "userName", userService.findById(buyer.getId()).getUserName()
        );

        // Mapear los Sellers a SellerResponseDto
        List<SellerResponseDto> sellersWithExtras = buyer.getFollowed().stream()
                .map(seller -> {
                    // Mapear cada Seller y añadirle los campos adicionales
                    SellerResponseDto sellerResponseDto = mapper.convertValue(seller, SellerResponseDto.class);

                    // Asignar el userName para cada Seller
                    sellerResponseDto.setUserName(userService.findById(seller.getId()).getUserName());

                    System.out.println("Mapeado Seller con ID: " + seller.getId() + " a SellerResponseDto");
                    return sellerResponseDto;
                })
                .collect(Collectors.toList());

        // Campos adicionales para Sellers
        Map<String, Object> sellerExtraFields = Map.of(
                "userName", sellersWithExtras.stream()
                        .map(SellerResponseDto::getUserName) // obtener userName de cada SellerResponseDto
                        .collect(Collectors.toList())
        );

        Map<String, Map<String, Object>> nestedExtras = Map.of(
                "followed", sellerExtraFields
        );

        // Log para verificar los Sellers mapeados
        System.out.println("Sellers mapeados a SellerResponseDto: " + sellersWithExtras);
        System.out.println("Campos adicionales para Sellers: " + sellerExtraFields);
        System.out.println("Campos anidados (nested extras) configurados para 'followed': " + nestedExtras);

        // Mapeo de Buyer a BuyerDto
        return mapToDtos.mapWithNestedExtraFields(
                buyer,
                BuyerDto.class,
                buyerExtraFields,
                nestedExtras
        );
    }

}
