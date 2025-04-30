package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IBuyerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.implementations.BuyerService;
import com.mercadolibre.be_java_hisp_w31_g07.util.BuyerFactory;
import com.mercadolibre.be_java_hisp_w31_g07.util.SellerFactory;
import com.mercadolibre.be_java_hisp_w31_g07.util.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BuyerServiceTest {
    @InjectMocks
    private BuyerService buyerService;

    @Mock
    private IBuyerRepository buyerRepository;

    @Mock
    private IUserService userService;

    private Seller seller;
    private Buyer buyer;
    private UserDto userDto;
    private UUID buyerId;
    private UUID sellerId;

    @BeforeEach
    void setUp() {
        seller = SellerFactory.createSeller();
        buyer = BuyerFactory.createBuyer();
        userDto = UserFactory.createUserDto();
        buyerId = buyer.getId();
        sellerId = seller.getId();
    }

    @Test
    void testFindFollowedSuccess() {
        when(buyerRepository.findBuyerById(buyerId)).thenReturn(Optional.of(buyer));
        when(userService.findById(buyerId)).thenReturn(userDto);

        BuyerDto result = buyerService.findFollowed(buyerId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(buyerId, result.getId()),
                () -> assertEquals(userDto.getUserName(), result.getUserName()),
                () -> assertEquals(buyer.getFollowed().size(), result.getFollowed().size())
        );
        verify(buyerRepository).findBuyerById(buyerId);
        verify(userService).findById(buyerId);
        verifyNoMoreInteractions(buyerRepository, userService);
    }


    @Test
    void testFindFollowedFail() {
        when(buyerRepository.findBuyerById(buyerId)).thenReturn(Optional.empty());

        BadRequest exception = assertThrows(BadRequest.class, () -> {
            buyerService.findFollowed(buyerId);
        });

        assertEquals("Buyer: " + buyerId + " not found", exception.getMessage());
        verify(buyerRepository).findBuyerById(buyerId);
        verifyNoInteractions(userService);
        verifyNoMoreInteractions(buyerRepository);
    }
}
