package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IBuyerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.implementations.BuyerService;
import com.mercadolibre.be_java_hisp_w31_g07.util.BuyerFactory;
import com.mercadolibre.be_java_hisp_w31_g07.util.ErrorMessagesUtil;
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

    private Buyer buyer;
    private UUID buyerId;

    @BeforeEach
    void setUp() {
        buyer = BuyerFactory.createBuyer();
        buyerId = buyer.getId();
    }

    @Test
    @DisplayName("[SUCCESS] Find buyer by ID")
    void testFindBuyerByIdSuccess() {
        when(buyerRepository.findBuyerById(buyerId)).thenReturn(Optional.of(buyer));

        Buyer result = buyerService.findBuyerById(buyerId);

        assertEquals(buyer, result);
        verify(buyerRepository).findBuyerById(buyerId);
        verifyNoMoreInteractions(buyerRepository);
    }

    @Test
    @DisplayName("[ERROR] Find buyer by ID - Not Found")
    void testFindBuyerByIdNotFound() {
        when(buyerRepository.findBuyerById(buyerId)).thenReturn(Optional.empty());

        BadRequest exception = assertThrows(BadRequest.class, () -> buyerService.findBuyerById(buyerId));

        assertEquals(ErrorMessagesUtil.buyerNotFound(buyerId), exception.getMessage());
        verify(buyerRepository).findBuyerById(buyerId);
        verifyNoMoreInteractions(buyerRepository);
    }

    @Test
    @DisplayName("[SUCCESS] Add seller to followed list")
    void testAddSellerToFollowedSuccess() {
        Seller seller = new Seller();
        seller.setId(UUID.randomUUID());

        when(buyerRepository.addSellerToFollowedList(seller, buyer.getId())).thenReturn(Optional.of(buyer));

        buyerService.addSellerToFollowed(seller, buyer);

        verify(buyerRepository).addSellerToFollowedList(seller, buyer.getId());
        verifyNoMoreInteractions(buyerRepository);
    }

    @Test
    @DisplayName("[ERROR] Add seller to followed list - Not Found")
    void testAddSellerToFollowedNotFound() {
        Seller seller = new Seller();
        seller.setId(UUID.randomUUID());

        when(buyerRepository.addSellerToFollowedList(seller, buyer.getId())).thenReturn(Optional.empty());

        BadRequest exception = assertThrows(BadRequest.class, () -> buyerService.addSellerToFollowed(seller, buyer));

        assertEquals(ErrorMessagesUtil.sellerNotFound(seller.getId()), exception.getMessage());
        verify(buyerRepository).addSellerToFollowedList(seller, buyer.getId());
        verifyNoMoreInteractions(buyerRepository);
    }

    @Test
    @DisplayName("[SUCCESS] Check if buyer is following seller")
    void testIsBuyerFollowingSeller() {
        Seller seller = new Seller();
        seller.setId(UUID.randomUUID());

        when(buyerRepository.buyerIsFollowingSeller(seller, buyerId)).thenReturn(true);

        boolean result = buyerService.isBuyerFollowingSeller(seller, buyerId);

        assertTrue(result);
        verify(buyerRepository).buyerIsFollowingSeller(seller, buyerId);
        verifyNoMoreInteractions(buyerRepository);
    }

    @Test
    @DisplayName("[SUCCESS] Check if buyer is not following seller")
    void testRemoveSellerFromFollowedList() {
        Seller seller = new Seller();
        seller.setId(UUID.randomUUID());

        doNothing().when(buyerRepository).removeSellerFromFollowedList(seller, buyerId);

        buyerService.removeSellerFromFollowedList(seller, buyerId);

        verify(buyerRepository).removeSellerFromFollowedList(seller, buyerId);
        verifyNoMoreInteractions(buyerRepository);
    }

}
