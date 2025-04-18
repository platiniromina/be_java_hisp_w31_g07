package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.SellerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.BuyerReponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.ISellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SellerService implements ISellerService {
    private final ISellerRepository sellerRepository;

    @Override
    public SellerDto findFollowers(UUID userId){
        ObjectMapper mapper = new ObjectMapper();

        Seller seller = sellerRepository.findFollowers(userId)
                .orElseThrow(()-> new NotFoundException("No se encontro el usuario"));

        return new SellerDto(seller.getId(), seller.getBillingAddress(),
                seller.getCuil(),seller.getFollowers().stream()
                .map(x->mapper.convertValue(x, BuyerReponseDto.class))
                .toList(), seller.getFollowerCount());
    }
}
