package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.SellerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.implementations.SellerService;
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
class SellerServiceTest {
    @InjectMocks
    private SellerService sellerService;

    @Mock
    private IBuyerService buyerService;

    @Mock
    private IUserService userService;

    @Mock
    private ISellerRepository sellerRepository;

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
    void testFindFollowersSuccess() {
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.of(seller));
        when(userService.findById(sellerId)).thenReturn(userDto);

        SellerDto result = sellerService.findFollowers(sellerId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(sellerId, result.getId()),
                () -> assertEquals(userDto.getUserName(), result.getUserName()),
                () -> assertEquals(seller.getFollowers().size(), result.getFollowerCount())
        );
        verify(sellerRepository).findSellerById(sellerId);
        verify(userService, times(2)).findById(sellerId);
        verifyNoMoreInteractions(sellerRepository, userService);
    }

    @Test
    void testFindFollowersBadRequest() {
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.empty());

        BadRequest exception = assertThrows(BadRequest.class, () -> {
            sellerService.findFollowers(sellerId);
        });

        assertEquals("Buyer: " + sellerId + " not found", exception.getMessage());
        verify(sellerRepository).findSellerById(sellerId);
        verifyNoInteractions(userService);
        verifyNoMoreInteractions(sellerRepository);
    }


}
