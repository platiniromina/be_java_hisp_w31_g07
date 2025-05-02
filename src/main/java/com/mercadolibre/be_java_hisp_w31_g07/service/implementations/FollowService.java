package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.mercadolibre.be_java_hisp_w31_g07.dto.request.BuyerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.SellerDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.request.UserDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerFollowersCountResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.model.Seller;
import com.mercadolibre.be_java_hisp_w31_g07.service.IBuyerService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IFollowService;
import com.mercadolibre.be_java_hisp_w31_g07.service.ISellerService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IUserService;
import com.mercadolibre.be_java_hisp_w31_g07.util.BuyerMapper;
import com.mercadolibre.be_java_hisp_w31_g07.util.ErrorMessagesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.mercadolibre.be_java_hisp_w31_g07.util.SortUtil.sortFollowers;
import static com.mercadolibre.be_java_hisp_w31_g07.util.UserUtil.initializeUsernameMap;

@Service
@RequiredArgsConstructor
public class FollowService implements IFollowService {

    private final ISellerService sellerService;
    private final IBuyerService buyerService;
    private final IUserService userService;

    // FOLLOWING ---------------------------

    @Override
    public void follow(UUID sellerId, UUID buyerId) {
        validateNotSameUser(sellerId, buyerId);

        Seller seller = sellerService.findSellerById(sellerId);
        Buyer buyer = buyerService.findBuyerById(buyerId);

        if (validateMutualFollowing(seller, buyer)) {
            throw new BadRequest(ErrorMessagesUtil.buyerAlreadyFollowingSeller(buyerId, sellerId));
        }

        executeFollow(seller, buyer);
    }

    private void executeFollow(Seller seller, Buyer buyer) {
        sellerService.addBuyerToFollowers(buyer, seller);
        buyerService.addSellerToFollowed(seller, buyer);
    }

    // UNFOLLOWING ---------------------------

    @Override
    public void unfollow(UUID sellerId, UUID buyerId) {
        validateNotSameUser(sellerId, buyerId);

        Seller seller = sellerService.findSellerById(sellerId);
        Buyer buyer = buyerService.findBuyerById(buyerId);

        if (!validateMutualFollowing(seller, buyer)) {
            throw new BadRequest(ErrorMessagesUtil.buyerNotFollowingSeller(buyerId, sellerId));
        }

        executeUnfollow(seller, buyer);
    }

    private void executeUnfollow(Seller seller, Buyer buyer) {
        sellerService.removeBuyerFromFollowersList(buyer, seller.getId());
        buyerService.removeSellerFromFollowedList(seller, buyer.getId());
    }

    // GET ACTIONS ---------------------------

    @Override
    public SellerDto findSortedFollowersByName(UUID sellerId, String order) {
        SellerFollowersResponse data = getFollowers(sellerId);
        List<Buyer> sortedFollowers = sortFollowers(data.followers(), order, userService);

        Seller seller = data.seller();
        seller.setFollowers(sortedFollowers);
        String sellerUsername = data.user().getUserName();

        Map<UUID, String> usernamesMap = userService.findUsernames(initializeUsernameMap(seller));
        return BuyerMapper.toSellerDto(seller, sellerUsername, usernamesMap);
    }

    @Override
    public SellerFollowersCountResponseDto findFollowersCount(UUID sellerId) {
        SellerFollowersResponse data = getFollowers(sellerId);
        Seller seller = data.seller();
        Integer count = seller.getFollowers().size();
        String userName = data.user().getUserName();
        return new SellerFollowersCountResponseDto(sellerId, userName, count);
    }

    @Override
    public SellerDto findFollowers(UUID sellerId) {
        SellerFollowersResponse data = getFollowers(sellerId);
        Seller seller = data.seller();
        String sellerUserName = data.user().getUserName();
        Map<UUID, String> usernamesMap = userService.findUsernames(initializeUsernameMap(seller));
        return BuyerMapper.toSellerDto(seller, sellerUserName, usernamesMap);
    }

    @Override
    public BuyerDto findFollowed(UUID buyerId) {
        Buyer buyer = buyerService.findBuyerById(buyerId);
        String buyerUserName = userService.findById(buyer.getId()).getUserName();
        return BuyerMapper.toBuyerDto(buyer, buyerUserName);
    }

    private SellerFollowersResponse getFollowers(UUID sellerId) {
        Seller seller = sellerService.findSellerById(sellerId);
        List<Buyer> followers = seller.getFollowers();
        UserDto user = userService.findById(seller.getId());
        return new SellerFollowersResponse(
                seller,
                followers,
                user
        );
    }

    // VALIDATIONS ---------------------------

    private void validateNotSameUser(UUID sellerId, UUID buyerId) {
        if (sellerId.equals(buyerId)) {
            throw new BadRequest(ErrorMessagesUtil.buyerAndSellerAreTheSame(sellerId, buyerId));
        }
    }

    private boolean validateMutualFollowing(Seller seller, Buyer buyer) {
        boolean isSellerFollowedByBuyer = sellerService.isSellerFollowedByBuyer(buyer, seller.getId());
        boolean isBuyerFollowingSeller = buyerService.isBuyerFollowingSeller(seller, buyer.getId());
        return isSellerFollowedByBuyer && isBuyerFollowingSeller;
    }

    // RECORDS ---------------------------

    public record SellerFollowersResponse(
            Seller seller,
            List<Buyer> followers,
            UserDto user
    ) {
    }

}

