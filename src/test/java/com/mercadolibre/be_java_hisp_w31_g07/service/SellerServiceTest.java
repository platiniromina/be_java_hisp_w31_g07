package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerFollowersCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.implementations.SellerService;
import com.mercadolibre.be_java_hisp_w31_g07.util.BuyerFactory;
import com.mercadolibre.be_java_hisp_w31_g07.util.SellerFactory;
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
class SellerServiceTest {
    @InjectMocks
    private SellerService sellerService;

    @Mock
    private IBuyerService buyerService;

    @Mock
    private ISellerRepository sellerRepository;

    @Mock
    private IUserService userService;

    private Seller seller;
    private Buyer buyer;
    private UUID buyerId;
    private UUID sellerId;

    @BeforeEach
    void setUp() {
        seller = SellerFactory.createSeller();
        buyer = BuyerFactory.createBuyer();
        buyerId = buyer.getId();
        sellerId = seller.getId();
    }

    @Test
    @DisplayName("[SUCCESS] Follow seller")
    void testFollowSellerSuccess() {
        stubValidBuyerAndSeller();

        sellerService.followSeller(sellerId, buyerId);

        verify(sellerRepository).addBuyerToFollowersList(buyer, sellerId);
        verify(buyerService).addSellerToFollowedList(seller, buyerId);
        verify(sellerRepository).sellerIsBeingFollowedByBuyer(buyer, sellerId);
        verify(buyerService).buyerIsFollowingSeller(seller, buyerId);
        verifyNoMoreInteractions(sellerRepository, buyerService);
    }

    @Test
    @DisplayName("[ERROR] Follow seller - buyer tries to follow themselves")
    void testFollowSellerSameUserError() {
        UUID sameId = UUID.randomUUID();

        BadRequest exception = assertThrows(BadRequest.class, () ->
                sellerService.followSeller(sameId, sameId)
        );

        assertEquals("User cannot follow themselves.", exception.getMessage());
        verifyNoInteractions(sellerRepository, buyerService);
    }

    @Test
    @DisplayName("[ERROR] Follow seller - buyer is already following seller")
    void testFollowSellerAlreadyFollowingError() {
        stubValidBuyerAndSeller();
        when(sellerRepository.sellerIsBeingFollowedByBuyer(buyer, sellerId)).thenReturn(true);
        when(buyerService.buyerIsFollowingSeller(seller, buyerId)).thenReturn(true);

        BadRequest exception = assertThrows(BadRequest.class, () ->
                sellerService.followSeller(sellerId, buyerId)
        );

        assertEquals("Buyer " + buyer.getId() + " already follows seller "
                + seller.getId(), exception.getMessage());
        verify(sellerRepository, never()).addBuyerToFollowersList(buyer, sellerId);
        verify(buyerService, never()).addSellerToFollowedList(seller, buyerId);
        verifyNoMoreInteractions(sellerRepository, buyerService);
    }

    @Test
    @DisplayName("[ERROR] Follow seller - seller not found")
    void testFollowSellerThatDoesNotExistError() {
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.empty());

        BadRequest exception = assertThrows(BadRequest.class, () ->
                sellerService.followSeller(sellerId, buyerId)
        );

        assertEquals("Seller " + sellerId + " not found", exception.getMessage());
        verify(sellerRepository, never()).addBuyerToFollowersList(buyer, sellerId);
        verify(buyerService, never()).addSellerToFollowedList(seller, buyerId);
        verifyNoMoreInteractions(sellerRepository, buyerService);
    }

    @Test
    @DisplayName("[ERROR] Follow seller - buyer not found")
    void testFollowSellerButBuyerNotFoundError() {
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.of(seller));
        when(buyerService.findBuyerById(buyerId)).thenThrow(new BadRequest("Buyer " + buyerId + " not found"));

        BadRequest exception = assertThrows(BadRequest.class, () ->
                sellerService.followSeller(sellerId, buyerId)
        );

        assertEquals("Buyer " + buyerId + " not found", exception.getMessage());
        verify(sellerRepository, never()).addBuyerToFollowersList(buyer, sellerId);
        verify(buyerService, never()).addSellerToFollowedList(seller, buyerId);
        verifyNoMoreInteractions(sellerRepository, buyerService);
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
    @DisplayName("[SUCCESS] Unfollow seller")
    void testUnfollowSellerSuccess() {
        stubValidBuyerAndSeller();
        when(sellerRepository.sellerIsBeingFollowedByBuyer(buyer, sellerId)).thenReturn(true);
        when(buyerService.buyerIsFollowingSeller(seller, buyerId)).thenReturn(true);

        sellerService.unfollowSeller(sellerId, buyerId);

        verify(sellerRepository).removeBuyerFromFollowersList(buyer, sellerId);
        verify(buyerService).removeSellerFromFollowedList(seller, buyerId);
        verifyNoMoreInteractions(sellerRepository, buyerService);
    }

    @Test
    @DisplayName("[ERROR] Unfollow seller - buyer is not following seller")
    void testUnfollowSellerWhoImNotFollowingError() {
        stubValidBuyerAndSeller();
        when(sellerRepository.sellerIsBeingFollowedByBuyer(buyer, sellerId)).thenReturn(false);
        when(buyerService.buyerIsFollowingSeller(seller, buyerId)).thenReturn(false);

        BadRequest exception = assertThrows(BadRequest.class, () ->
                sellerService.unfollowSeller(sellerId, buyerId)
        );

        assertEquals("Buyer " + buyer.getId() + " is not following seller "
                + seller.getId() + ". Unfollow action cannot be done", exception.getMessage());
        verify(sellerRepository, never()).removeBuyerFromFollowersList(buyer, sellerId);
        verify(buyerService, never()).removeSellerFromFollowedList(seller, buyerId);
        verifyNoMoreInteractions(sellerRepository, buyerService);
    }

    @Test
    @DisplayName("[ERROR] Unfollow seller - seller not found")
    void testUnfollowSellerThatDoesNotExistError() {
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.empty());

        BadRequest exception = assertThrows(BadRequest.class, () ->
                sellerService.unfollowSeller(sellerId, buyerId)
        );

        assertEquals("Seller " + sellerId + " not found", exception.getMessage());

        verify(sellerRepository, never()).removeBuyerFromFollowersList(buyer, sellerId);
        verify(buyerService, never()).removeSellerFromFollowedList(seller, buyerId);
        verifyNoMoreInteractions(sellerRepository, buyerService);
    }

    @Test
    @DisplayName("[ERROR] Unfollow seller - buyer not found")
    void testUnfollowSellerButBuyerNotFoundError() {
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.of(seller));
        when(buyerService.findBuyerById(buyerId)).thenThrow(new BadRequest("Buyer " + buyerId + " not found"));

        BadRequest exception = assertThrows(BadRequest.class, () ->
                sellerService.unfollowSeller(sellerId, buyerId)
        );

        assertEquals("Buyer " + buyerId + " not found", exception.getMessage());

        verify(sellerRepository, never()).removeBuyerFromFollowersList(buyer, sellerId);
        verify(buyerService, never()).removeSellerFromFollowedList(seller, buyerId);
        verifyNoMoreInteractions(sellerRepository, buyerService);
    }

    @Test
    @DisplayName("[SUCCESS] Find followers")
    void testFindFollowersCountSuccess() {
        Seller sellerWithFollowers = SellerFactory.createSellerWithFollowers(3);
        sellerWithFollowers.setId(sellerId);
        UserDto userDto = UserFactory.createUserDto();
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.of(sellerWithFollowers));
        when(userService.findById(sellerId)).thenReturn(userDto);

        SellerFollowersCountResponseDto result = sellerService.findFollowersCount(sellerId);

        assertAll(
                () -> assertEquals(sellerId, result.getUser_id()),
                () -> assertEquals(userDto.getUserName(), result.getUser_name()),
                () -> assertEquals(sellerWithFollowers.getFollowers().size(), result.getFollowersCount()));
        verify(sellerRepository).findSellerById(sellerId);
        verify(userService).findById(sellerId);
        verifyNoMoreInteractions(sellerRepository, userService);
    }

    @Test
    @DisplayName("[ERROR] Find followers - seller not found")
    void testFindFollowersCountSellerNotFoundError() {
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.empty());

        BadRequest exception = assertThrows(BadRequest.class, () -> sellerService.findFollowersCount(sellerId));

        assertEquals("Seller " + sellerId + " not found", exception.getMessage());
        verify(sellerRepository).findSellerById(sellerId);
        verifyNoMoreInteractions(sellerRepository, userService);
    }

    private void stubValidBuyerAndSeller() {
        when(buyerService.findBuyerById(buyerId)).thenReturn(buyer);
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.of(seller));
    }
}
