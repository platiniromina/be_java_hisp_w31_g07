package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IBuyerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IUserRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IBuyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuyerService implements IBuyerService {
    private final IBuyerRepository buyerRepository;
    private final IUserRepository userRepository;

    @Override
    public BuyerDto findFollowed(UUID userId){
        ObjectMapper mapper = new ObjectMapper();
        Buyer buyer = buyerRepository.findFollowed(userId).orElseThrow(()-> new NotFoundException("No se encontro al usuario"));

        return new BuyerDto(buyer.getId(),
                buyer.getFollowed().stream()
                        .map(x->mapper.convertValue(x, SellerResponseDto.class))
                        .toList());
    }
}
