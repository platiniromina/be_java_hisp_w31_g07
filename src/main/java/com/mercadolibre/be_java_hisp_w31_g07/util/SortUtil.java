package com.mercadolibre.be_java_hisp_w31_g07.util;

import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.BadRequest;
import com.mercadolibre.be_java_hisp_w31_g07.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g07.service.IUserService;
import lombok.experimental.UtilityClass;

import java.util.Comparator;
import java.util.List;

@UtilityClass
public class SortUtil {

    public static List<Buyer> sortFollowers(List<Buyer> followers, String order, IUserService userService) {
        Comparator<Buyer> comparator = getComparatorForOrder(order, userService);
        return followers.stream()
                .sorted(comparator)
                .toList();
    }

    public static Comparator<Buyer> getComparatorForOrder(String order, IUserService userService) {
        Comparator<Buyer> comparator = Comparator.comparing(
                buyer -> userService.findById(buyer.getId()).getUserName());

        return switch (order.toLowerCase()) {
            case "name_desc" -> comparator.reversed();
            case "name_asc" -> comparator;
            default -> throw new BadRequest(ErrorMessagesUtil.invalidSortingParameter(order));
        };
    }

    public static List<PostResponseDto> sortByDate(List<PostResponseDto> posts, String order) {
        return switch (order.toLowerCase()) {
            case "date_desc" -> posts.stream()
                    .sorted(Comparator.comparing(PostResponseDto::getDate).reversed())
                    .toList();
            case "date_asc" -> posts.stream()
                    .sorted(Comparator.comparing(PostResponseDto::getDate))
                    .toList();
            default -> throw new BadRequest(ErrorMessagesUtil.invalidSortingParameter(order));
        };
    }
}
