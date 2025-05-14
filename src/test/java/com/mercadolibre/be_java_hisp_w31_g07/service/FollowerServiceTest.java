package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.SellerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerFollowersCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.model.User;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.implementations.FollowService;
import com.mercadolibre.be_java_hisp_w31_g07.util.BuyerFactory;
import com.mercadolibre.be_java_hisp_w31_g07.util.ErrorMessagesUtil;
import com.mercadolibre.be_java_hisp_w31_g07.util.SellerFactory;
import com.mercadolibre.be_java_hisp_w31_g07.util.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static com.mercadolibre.be_java_hisp_w31_g07.util.UserUtil.initializeUsernameMap;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowerServiceTest {

    @InjectMocks
    private FollowService followService;

    @Mock
    private IBuyerService buyerService;

    @Mock
    private IUserService userService;

    @Mock
    private ISellerService sellerService;

    @Mock
    private ISellerRepository sellerRepository;

    private Seller seller;
    private UUID sellerId;
    private UserDto userSellerDto;

    private Buyer buyer;
    private UUID buyerId;
    private User userBuyer;
    private UserDto userBuyerDto;

    private Buyer buyer2;
    private User userBuyer2;
    private UserDto userBuyer2Dto;

    @BeforeEach
    void setUp() {
        User userSeller = UserFactory.createUser(null);
        userSellerDto = UserFactory.createUserDto(userSeller.getId());
        seller = SellerFactory.createSeller(userSeller.getId());
        sellerId = seller.getId();

        userBuyer = UserFactory.createUser(null);
        userBuyerDto = UserFactory.createUserDto(userBuyer.getId());
        buyer = BuyerFactory.createBuyer(userBuyer.getId());
        buyerId = buyer.getId();

        userBuyer2 = UserFactory.createUser(null);
        userBuyer2Dto = UserFactory.createUserDto(userBuyer2.getId());
        buyer2 = BuyerFactory.createBuyer(userBuyer2.getId());
    }

    @Test
    @DisplayName("[SUCCESS] Find followers")
    void testFindFollowersSuccess() {
        when(sellerService.findSellerById(sellerId)).thenReturn(seller);
        when(userService.findById(sellerId)).thenReturn(userSellerDto);
        when(userService.findUsernames(initializeUsernameMap(seller)))
                .thenReturn(Map.of(sellerId, userSellerDto.getUserName()));


        SellerDto result = followService.findFollowers(sellerId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(sellerId, result.getId()),
                () -> assertEquals(userSellerDto.getUserName(), result.getUserName()),
                () -> assertEquals(seller.getFollowers().size(), result.getFollowerCount())
        );
        verify(sellerService).findSellerById(sellerId);
        verify(userService).findById(sellerId);
        verify(userService).findUsernames(initializeUsernameMap(seller));
        verifyNoMoreInteractions(sellerService, userService);
    }

    @Test
    @DisplayName("[Error] Find followers - a seller with that id does not exist")
    void testFindFollowersBadRequest() {
        when(sellerService.findSellerById(sellerId))
                .thenThrow(new BadRequest(ErrorMessagesUtil.sellerNotFound(sellerId)));

        BadRequest exception = assertThrows(BadRequest.class, () -> followService.findFollowers(sellerId));

        assertEquals(ErrorMessagesUtil.sellerNotFound(sellerId), exception.getMessage());
        verify(sellerService).findSellerById(sellerId);
        verifyNoInteractions(userService);
        verifyNoMoreInteractions(sellerService);
    }

    @Test
    @DisplayName("[SUCCESS] Follow seller")
    void testFollowSellerSuccess() {
        stubValidBuyerAndSeller();

        followService.follow(sellerId, buyerId);

        verify(sellerService).addBuyerToFollowers(buyer, seller);
        verify(buyerService).addSellerToFollowed(seller, buyer);
        verify(sellerService).isSellerFollowedByBuyer(buyer, sellerId);
        verify(buyerService).isBuyerFollowingSeller(seller, buyerId);
        verifyNoMoreInteractions(sellerRepository, buyerService);
    }

    @Test
    @DisplayName("[ERROR] Follow seller - buyer tries to follow themselves")
    void testFollowSellerSameUserError() {
        UUID sameId = UUID.randomUUID();

        BadRequest exception = assertThrows(BadRequest.class, () ->
                followService.follow(sameId, sameId)
        );

        assertEquals(ErrorMessagesUtil.buyerAndSellerAreTheSame(sameId, sameId), exception.getMessage());

        verifyNoInteractions(sellerService, buyerService);
    }

    @Test
    @DisplayName("[ERROR] Follow seller - buyer is already following seller")
    void testFollowSellerAlreadyFollowingError() {
        stubValidBuyerAndSeller();
        when(sellerService.isSellerFollowedByBuyer(buyer, sellerId)).thenReturn(true);
        when(buyerService.isBuyerFollowingSeller(seller, buyerId)).thenReturn(true);

        BadRequest exception = assertThrows(BadRequest.class, () ->
                followService.follow(sellerId, buyerId)
        );

        assertEquals(ErrorMessagesUtil.buyerAlreadyFollowingSeller(buyerId, sellerId), exception.getMessage());

        verify(sellerService, never()).addBuyerToFollowers(buyer, seller);
        verify(buyerService, never()).addSellerToFollowed(seller, buyer);
        verifyNoMoreInteractions(sellerRepository, buyerService);
    }

    @Test
    @DisplayName("[ERROR] Follow seller - seller not found")
    void testFollowSellerThatDoesNotExistError() {
        when(sellerService.findSellerById(sellerId)).thenThrow(new BadRequest(ErrorMessagesUtil.sellerNotFound(sellerId)));

        BadRequest exception = assertThrows(BadRequest.class, () ->
                followService.follow(sellerId, buyerId)
        );

        assertEquals(ErrorMessagesUtil.sellerNotFound(sellerId), exception.getMessage());
        verify(sellerService, never()).addBuyerToFollowers(buyer, seller);
        verify(buyerService, never()).addSellerToFollowed(seller, buyer);
        verifyNoMoreInteractions(sellerService, buyerService);
    }

    @Test
    @DisplayName("[ERROR] Follow seller - buyer not found")
    void testFollowSellerButBuyerNotFoundError() {
        when(sellerService.findSellerById(sellerId)).thenReturn(seller);
        when(buyerService.findBuyerById(buyerId)).thenThrow(new BadRequest(ErrorMessagesUtil.buyerNotFound(buyerId)));

        BadRequest exception = assertThrows(BadRequest.class, () -> followService.follow(sellerId, buyerId));

        assertEquals(ErrorMessagesUtil.buyerNotFound(buyerId), exception.getMessage());

        verify(sellerService, never()).addBuyerToFollowers(buyer, seller);
        verify(buyerService, never()).addSellerToFollowed(seller, buyer);
        verifyNoMoreInteractions(sellerService, buyerService);
    }

    @Test
    @DisplayName("[SUCCESS] Unfollow seller")
    void testUnfollowSellerSuccess() {
        stubValidBuyerAndSeller();
        when(sellerService.isSellerFollowedByBuyer(buyer, sellerId)).thenReturn(true);
        when(buyerService.isBuyerFollowingSeller(seller, buyerId)).thenReturn(true);

        followService.unfollow(sellerId, buyerId);

        verify(sellerService).removeBuyerFromFollowersList(buyer, sellerId);
        verify(buyerService).removeSellerFromFollowedList(seller, buyerId);
        verifyNoMoreInteractions(sellerService, buyerService);
    }

    @Test
    @DisplayName("[ERROR] Unfollow seller - buyer is not following seller")
    void testUnfollowSellerWhoImNotFollowingError() {
        stubValidBuyerAndSeller();
        when(sellerService.isSellerFollowedByBuyer(buyer, sellerId)).thenReturn(false);
        when(buyerService.isBuyerFollowingSeller(seller, buyerId)).thenReturn(false);

        BadRequest exception = assertThrows(BadRequest.class, () ->
                followService.unfollow(sellerId, buyerId)
        );

        assertEquals(ErrorMessagesUtil.buyerNotFollowingSeller(buyerId, sellerId), exception.getMessage());
        verify(sellerService, never()).removeBuyerFromFollowersList(buyer, sellerId);
        verify(buyerService, never()).removeSellerFromFollowedList(seller, buyerId);
        verifyNoMoreInteractions(sellerRepository, buyerService);
    }

    @Test
    @DisplayName("[ERROR] Unfollow seller - seller not found")
    void testUnfollowSellerThatDoesNotExistError() {
        when(sellerService.findSellerById(sellerId)).thenThrow(new BadRequest(ErrorMessagesUtil.sellerNotFound(sellerId)));

        BadRequest exception = assertThrows(BadRequest.class, () ->
                followService.unfollow(sellerId, buyerId)
        );

        assertEquals(ErrorMessagesUtil.sellerNotFound(sellerId), exception.getMessage());

        verify(sellerService, never()).removeBuyerFromFollowersList(buyer, sellerId);
        verify(buyerService, never()).removeSellerFromFollowedList(seller, buyerId);
        verifyNoMoreInteractions(sellerService, buyerService);
    }

    @Test
    @DisplayName("[ERROR] Unfollow seller - buyer not found")
    void testUnfollowSellerButBuyerNotFoundError() {
        when(sellerService.findSellerById(sellerId)).thenReturn(seller);
        when(buyerService.findBuyerById(buyerId)).thenThrow(new BadRequest(ErrorMessagesUtil.buyerNotFound(buyerId)));

        BadRequest exception = assertThrows(BadRequest.class, () ->
                followService.unfollow(sellerId, buyerId)
        );

        assertEquals(ErrorMessagesUtil.buyerNotFound(buyerId), exception.getMessage());

        verify(sellerService, never()).removeBuyerFromFollowersList(buyer, sellerId);
        verify(buyerService, never()).removeSellerFromFollowedList(seller, buyerId);
        verifyNoMoreInteractions(sellerService, buyerService);
    }

    @Test
    @DisplayName("[SUCCESS] Find followers count")
    void testFindFollowersCountSuccess() {
        Seller sellerWithFollowers = SellerFactory.createSellerWithFollowers(3);
        sellerWithFollowers.setId(sellerId);
        when(sellerService.findSellerById(sellerId)).thenReturn(sellerWithFollowers);
        when(userService.findById(sellerId)).thenReturn(userSellerDto);

        SellerFollowersCountResponseDto result = followService.findFollowersCount(sellerId);

        assertAll(
                () -> assertEquals(sellerId, result.getUserId()),
                () -> assertEquals(userSellerDto.getUserName(), result.getUserName()),
                () -> assertEquals(sellerWithFollowers.getFollowers().size(), result.getFollowersCount()));
        verify(sellerService).findSellerById(sellerId);
        verify(userService).findById(sellerId);
        verifyNoMoreInteractions(sellerService, userService);
    }

    @Test
    @DisplayName("[ERROR] Find followers count - seller not found")
    void testFindFollowersCountSellerNotFoundError() {
        when(sellerService.findSellerById(sellerId)).thenThrow(new BadRequest(ErrorMessagesUtil.sellerNotFound(sellerId)));

        BadRequest exception = assertThrows(BadRequest.class, () -> followService.findFollowersCount(sellerId));

        assertEquals(ErrorMessagesUtil.sellerNotFound(sellerId), exception.getMessage());
        verify(sellerService).findSellerById(sellerId);
        verifyNoMoreInteractions(sellerService, userService);
    }

    @Test
    @DisplayName("[SUCCESS] Find followed")
    void testFindFollowedSuccess() {
        when(buyerService.findBuyerById(buyerId)).thenReturn(buyer);
        when(userService.findById(buyerId)).thenReturn(userBuyerDto);

        BuyerDto result = followService.findFollowed(buyerId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(buyerId, result.getId()),
                () -> assertEquals(userBuyerDto.getUserName(), result.getUserName()),
                () -> assertEquals(buyer.getFollowed().size(), result.getFollowed().size())
        );
        verify(buyerService).findBuyerById(buyerId);
        verify(userService).findById(buyerId);
        verifyNoMoreInteractions(buyerService, userService);
    }

    @Test
    @DisplayName("[ERROR] Find followed - a buyer with that buyerId does not exist.")
    void testFindFollowedFail() {
        when(buyerService.findBuyerById(buyerId))
                .thenThrow(new BadRequest(ErrorMessagesUtil.buyerNotFound(buyerId)));

        BadRequest exception = assertThrows(BadRequest.class, () -> followService.findFollowed(buyerId));

        assertEquals(ErrorMessagesUtil.buyerNotFound(buyerId), exception.getMessage());
        verify(buyerService).findBuyerById(buyerId);
        verifyNoInteractions(userService);
        verifyNoMoreInteractions(buyerService);
    }

    @Test
    @DisplayName("[OK] Sort followers by name - ascending order")
    void testSortFollowersByNameAsc() {
        userBuyer.setUserName("A");
        userBuyer2.setUserName("B");
        seller.addFollower(buyer);
        seller.addFollower(buyer2);
        userBuyerDto.setUserName("A");
        userBuyer2Dto.setUserName("B");

        Map<UUID, String> usernameMap = initializeUsernameMap(seller);
        usernameMap.put(userBuyer.getId(), userBuyerDto.getUserName());
        usernameMap.put(userBuyer2.getId(), userBuyer2Dto.getUserName());

        when(sellerService.findSellerById(sellerId)).thenReturn(seller);
        when(userService.findById(sellerId)).thenReturn(userSellerDto);
        when(userService.findUsernames(initializeUsernameMap(seller)))
                .thenReturn(usernameMap);
        when(userService.findById(userBuyer.getId())).thenReturn(userBuyerDto);
        when(userService.findById(userBuyer2.getId())).thenReturn(userBuyer2Dto);

        SellerDto result = followService.findSortedFollowersByName(sellerId, "name_asc");

        assertAll(
                () -> assertEquals("A", result.getFollowers().get(0).getUserName()),
                () -> assertEquals("B", result.getFollowers().get(1).getUserName())
        );
        verify(sellerService).findSellerById(sellerId);
        verify(userService).findById(sellerId);
        verify(userService).findUsernames(initializeUsernameMap(seller));
        verify(userService).findById(userBuyer.getId());
        verify(userService).findById(userBuyer2.getId());
        verifyNoMoreInteractions(sellerService, userService);
    }


    @DisplayName("[OK] Sort followers by name - descending order")
    @Test
    void testSortFollowersByNameDesc() {
        userBuyer.setUserName("A");
        userBuyer2.setUserName("B");
        seller.addFollower(buyer);
        seller.addFollower(buyer2);
        userBuyerDto.setUserName("A");
        userBuyer2Dto.setUserName("B");

        Map<UUID, String> usernameMap = initializeUsernameMap(seller);
        usernameMap.put(userBuyer.getId(), userBuyerDto.getUserName());
        usernameMap.put(userBuyer2.getId(), userBuyer2Dto.getUserName());

        when(userService.findById(seller.getId())).thenReturn(userSellerDto);
        when(sellerService.findSellerById(sellerId)).thenReturn(seller);
        when(userService.findById(userBuyer2.getId())).thenReturn(userBuyer2Dto);
        when(userService.findById(userBuyer.getId())).thenReturn(userBuyerDto);
        when(userService.findUsernames(initializeUsernameMap(seller)))
                .thenReturn(usernameMap);

        SellerDto result = followService.findSortedFollowersByName(sellerId, "name_desc");

        assertAll(
                () -> assertEquals("B", result.getFollowers().get(0).getUserName()),
                () -> assertEquals("A", result.getFollowers().get(1).getUserName())
        );
        verify(sellerService).findSellerById(sellerId);
        verify(userService).findById(sellerId);
        verify(userService).findById(userBuyer.getId());
        verify(userService).findById(userBuyer2.getId());
        verifyNoMoreInteractions(sellerService, userService);
    }


    @DisplayName("[ERROR] Sort followers by name - seller has no followers")
    @Test
    void testSortFollowersByNameNoFollowers() {
        when(userService.findById(seller.getId())).thenReturn(userSellerDto);
        when(sellerService.findSellerById(sellerId)).thenReturn(seller);
        when(userService.findUsernames(initializeUsernameMap(seller)))
                .thenReturn(initializeUsernameMap(seller));

        SellerDto result = followService.findSortedFollowersByName(sellerId, "name_asc");

        assertEquals(userSellerDto.getUserName(), result.getUserName());
        assertTrue(result.getFollowers().isEmpty(), "Followers list should be empty");

        verify(sellerService).findSellerById(sellerId);
        verify(userService).findById(sellerId);
        verify(userService).findUsernames(initializeUsernameMap(seller));
        verifyNoMoreInteractions(sellerService, userService);
    }

    @DisplayName("[ERROR] Sort followers by name - invalid order parameter")
    @Test
    void testSortFollowersByName_InvalidOrderParam() {
        when(userService.findById(seller.getId())).thenReturn(userSellerDto);
        when(sellerService.findSellerById(sellerId)).thenReturn(seller);

        BadRequest exception = assertThrows(BadRequest.class, () ->
                followService.findSortedFollowersByName(sellerId, "invalid_order")
        );

        assertEquals(ErrorMessagesUtil.invalidSortingParameter("invalid_order"), exception.getMessage());
        verify(sellerService).findSellerById(sellerId);
        verify(userService).findById(sellerId);
        verifyNoMoreInteractions(sellerService, userService);
    }

    private void stubValidBuyerAndSeller() {
        when(buyerService.findBuyerById(buyerId)).thenReturn(buyer);
        when(sellerService.findSellerById(sellerId)).thenReturn(seller);
    }
}





