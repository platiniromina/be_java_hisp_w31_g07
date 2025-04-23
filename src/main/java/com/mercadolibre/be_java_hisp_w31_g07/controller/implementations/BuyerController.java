package com.mercadolibre.be_java_hisp_w31_g07.controller.implementations;


import com.mercadolibre.be_java_hisp_w31_g07.controller.IBuyerController;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.service.IBuyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BuyerController implements IBuyerController {

    private final IBuyerService buyerService;

    @Override
    public ResponseEntity<BuyerDto> getFollowed(UUID userId) {
        return new ResponseEntity<>(buyerService.findFollowed(userId), HttpStatus.OK);
    }
}
