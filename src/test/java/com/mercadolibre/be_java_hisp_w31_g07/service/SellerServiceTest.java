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
import org.junit.jupiter.api.Assertions;
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
import static org.springframework.test.util.AssertionErrors.assertEquals;

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
                () -> Assertions.assertEquals(sellerId, result.getId()),
                () -> Assertions.assertEquals(userDto.getUserName(), result.getUserName()),
                () -> Assertions.assertEquals(seller.getFollowers().size(), result.getFollowerCount())
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

        Assertions.assertEquals("Seller: " + sellerId + " not found", exception.getMessage());
        verify(sellerRepository).findSellerById(sellerId);
        verifyNoInteractions(userService);
        verifyNoMoreInteractions(sellerRepository);
    }

    @Test
    void testFollowSellerSuccess() {
        stubValidBuyerAndSeller();

        sellerService.followSeller(sellerId, buyerId);

        verify(sellerRepository).addBuyerToFollowersList(buyer, sellerId);
        verify(buyerService).addSellerToFollowedList(seller, buyerId);
    }

    @Test
    void testFollowSellerSameUserError() {
        UUID sameId = UUID.randomUUID();

        BadRequest exception = assertThrows(BadRequest.class, () ->
                sellerService.followSeller(sameId, sameId)
        );

        assertEquals("Error message mismatch", "User cannot follow themselves.", exception.getMessage());
        verifyNoInteractions(sellerRepository, buyerService);
    }

    @Test
    void testFollowSellerAlreadyFollowingError() {
        stubValidBuyerAndSeller();
        when(sellerRepository.sellerIsBeingFollowedByBuyer(buyer, sellerId)).thenReturn(true);
        when(buyerService.buyerIsFollowingSeller(seller, buyerId)).thenReturn(true);

        BadRequest exception = assertThrows(BadRequest.class, () ->
                sellerService.followSeller(sellerId, buyerId)
        );

        assertEquals("Error message mismatch", "Buyer " + buyer.getId() + " already follows seller "
                + seller.getId(), exception.getMessage());
        verify(sellerRepository, never()).addBuyerToFollowersList(buyer, sellerId);
        verify(buyerService, never()).addSellerToFollowedList(seller, buyerId);
    }

    @Test
    void testFollowSellerThatDoesNotExistError() {
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.empty());

        BadRequest exception = assertThrows(BadRequest.class, () ->
                sellerService.followSeller(sellerId, buyerId)
        );

        assertEquals("Error message mismatch", "Seller " + sellerId + " not found", exception.getMessage());
        verify(sellerRepository, never()).addBuyerToFollowersList(buyer, sellerId);
        verify(buyerService, never()).addSellerToFollowedList(seller, buyerId);
    }

    @Test
    void testFollowSellerButBuyerNotFoundError() {
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.of(seller));
        when(buyerService.findBuyerById(buyerId)).thenThrow(new BadRequest("Buyer " + buyerId + " not found"));

        BadRequest exception = assertThrows(BadRequest.class, () ->
                sellerService.followSeller(sellerId, buyerId)
        );

        assertEquals("Error message mismatch", "Buyer " + buyerId + " not found", exception.getMessage());
        verify(sellerRepository, never()).addBuyerToFollowersList(buyer, sellerId);
        verify(buyerService, never()).addSellerToFollowedList(seller, buyerId);
    }

    @Test
    void testFindSellerByIdSuccess() {
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.of(seller));

        Seller foundSeller = sellerService.findSellerById(sellerId);

        assertEquals("Seller ID mismatch", sellerId, foundSeller.getId());
        verify(sellerRepository).findSellerById(sellerId);
        verifyNoMoreInteractions(sellerRepository);
    }

    @Test
    void testFindSellerByIdNotFoundError() {
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.empty());

        BadRequest exception = assertThrows(BadRequest.class, () ->
                sellerService.findSellerById(sellerId)
        );

        assertEquals("Error message mismatch", "Seller " + sellerId + " not found", exception.getMessage());
        verify(sellerRepository).findSellerById(sellerId);
        verifyNoMoreInteractions(sellerRepository);
    }

    private void stubValidBuyerAndSeller() {
        when(buyerService.findBuyerById(buyerId)).thenReturn(buyer);
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.of(seller));
    }
}
