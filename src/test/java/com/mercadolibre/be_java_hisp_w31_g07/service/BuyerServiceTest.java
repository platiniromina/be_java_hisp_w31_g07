package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IBuyerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.implementations.BuyerService;
import com.mercadolibre.be_java_hisp_w31_g07.util.BuyerFactory;
import com.mercadolibre.be_java_hisp_w31_g07.util.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class BuyerServiceTest {
    @InjectMocks
    private BuyerService buyerService;

    @Mock
    private IBuyerRepository buyerRepository;

    @Mock
    private IUserService userService;

    private Buyer buyer;
    private UserDto userDto;
    private UUID buyerId;

    @BeforeEach
    void setUp() {
        buyer = BuyerFactory.createBuyer();
        userDto = UserFactory.createUserDto();
        buyerId = buyer.getId();
    }

    @Test
    @DisplayName("[SUCCESS] Find followed")
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
    @DisplayName("[ERROR] Find followed - a buyer with that buyerId does not exist.")
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
