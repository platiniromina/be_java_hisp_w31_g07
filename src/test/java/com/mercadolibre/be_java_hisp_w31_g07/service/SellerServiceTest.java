package com.mercadolibre.be_java_hisp_w31_g07.service;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerFollowersCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.repository.ISellerRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.implementations.SellerService;
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
    private ISellerRepository sellerRepository;

    @Mock
    private IUserService userService;

    private Seller seller;
    private UUID sellerId;

    @BeforeEach
    void setUp() {
        seller = SellerFactory.createSeller();
        sellerId = seller.getId();
    }

    @Test
    void testFindFollowersCountSuccess() {
        Seller sellerWithFollowers = SellerFactory.createSellerWithFollowers(3);
        sellerWithFollowers.setId(sellerId);
        UserDto userDto = UserFactory.createUserDto();
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.of(seller));
        when(userService.findById(sellerId)).thenReturn(userDto);

        SellerFollowersCountResponseDto result = sellerService.findFollowersCount(sellerId);

        assertAll(
                () -> assertEquals(sellerId, result.getUser_id()),
                () -> assertEquals(userDto.getUserName(), result.getUser_name()),
                () -> assertEquals(seller.getFollowers().size(), result.getFollowersCount())
        );
        verify(sellerRepository).findSellerById(sellerId);
        verify(userService).findById(sellerId);
        verifyNoMoreInteractions(sellerRepository, userService);
    }

    @Test
    void testFindFollowersCountSellerNotFoundError() {
        when(sellerRepository.findSellerById(sellerId)).thenReturn(Optional.empty());

        BadRequest exception = assertThrows(BadRequest.class, () ->
                sellerService.findFollowersCount(sellerId)
        );

        assertEquals("Seller " + sellerId + " not found", exception.getMessage());
        verify(sellerRepository).findSellerById(sellerId);
        verifyNoMoreInteractions(sellerRepository, userService);
    }
}