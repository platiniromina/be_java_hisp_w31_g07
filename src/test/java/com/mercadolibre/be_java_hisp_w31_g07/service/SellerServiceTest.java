package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.implementations.SellerService;
import com.mercadolibre.be_java_hisp_w31_g07.util.ErrorMessagesUtil;
import com.mercadolibre.be_java_hisp_w31_g07.util.SellerFactory;
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
class SellerServiceTest {

    @InjectMocks
    private SellerService sellerService;

    @Mock
    private ISellerRepository sellerRepository;

    private Seller seller;
    private UUID sellerId;

    @BeforeEach
    void setUp() {
        seller = SellerFactory.createSeller();
        sellerId = seller.getId();
    }

    @Test
    @DisplayName("[SUCCESS] Find seller")
    void testFindSellerByIdSuccess() {
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.of(seller));

        Seller foundSeller = sellerService.findSellerById(sellerId);

        assertEquals(sellerId, foundSeller.getId());

        verify(sellerRepository).findSellerById(sellerId);
        verifyNoMoreInteractions(sellerRepository);
    }

    @Test
    @DisplayName("[ERROR] Find seller - seller not found")
    void testFindSellerByIdNotFoundError() {
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.empty());

        BadRequest exception = assertThrows(BadRequest.class, () ->
                sellerService.findSellerById(sellerId)
        );

        assertEquals("Seller " + sellerId + " not found", exception.getMessage());
        verify(sellerRepository).findSellerById(sellerId);
        verifyNoMoreInteractions(sellerRepository);
    }

    @Test
    @DisplayName("[SUCCESS] Add buyer to followers list")
    void testAddBuyerToFollowersList() {
        Buyer buyer = new Buyer();
        buyer.setId(UUID.randomUUID());

        sellerService.addBuyerToFollowers(buyer, seller);

        verify(sellerRepository).addBuyerToFollowersList(buyer, seller.getId());
        verifyNoMoreInteractions(sellerRepository);
    }

    @Test
    @DisplayName("[SUCCESS] Remove buyer from followers list")
    void testRemoveBuyerFromFollowersList() {
        Buyer buyer = new Buyer();
        buyer.setId(UUID.randomUUID());

        sellerService.removeBuyerFromFollowersList(buyer, sellerId);

        verify(sellerRepository).removeBuyerFromFollowersList(buyer, sellerId);
        verifyNoMoreInteractions(sellerRepository);
    }

    @Test
    @DisplayName("[SUCCESS] Check if seller is followed by buyer")
    void testIsSellerFollowedByBuyer() {
        Buyer buyer = new Buyer();
        buyer.setId(UUID.randomUUID());

        when(sellerRepository.sellerIsBeingFollowedByBuyer(buyer, sellerId)).thenReturn(true);

        boolean isFollowed = sellerService.isSellerFollowedByBuyer(buyer, sellerId);

        assertTrue(isFollowed);
        verify(sellerRepository).sellerIsBeingFollowedByBuyer(buyer, sellerId);
        verifyNoMoreInteractions(sellerRepository);
    }

    @Test
    @DisplayName("[SUCCESS] find seller by id")
    void testFindSellerById() {
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.of(seller));

        Seller foundSeller = sellerService.findSellerById(sellerId);

        assertEquals(seller, foundSeller);
        verify(sellerRepository).findSellerById(sellerId);
        verifyNoMoreInteractions(sellerRepository);
    }

    @Test
    @DisplayName("[ERROR] find seller by id - not found")
    void testFindSellerByIdNotFound() {
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.empty());

        BadRequest exception = assertThrows(BadRequest.class, () -> sellerService.findSellerById(sellerId));

        assertEquals(ErrorMessagesUtil.sellerNotFound(sellerId), exception.getMessage());
        verify(sellerRepository).findSellerById(sellerId);
        verifyNoMoreInteractions(sellerRepository);
    }

}
