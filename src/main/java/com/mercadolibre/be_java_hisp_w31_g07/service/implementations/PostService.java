package com.mercadolibre.be_java_hisp_w31_g07.service.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.dto.response.SellerResponseDto;
import com.mercadolibre.be_java_hisp_w31_g07.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IPostRepository;
import com.mercadolibre.be_java_hisp_w31_g07.service.IBuyerService;
import com.mercadolibre.be_java_hisp_w31_g07.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final IPostRepository postRepository;
    private final IBuyerService buyerService;

    @Override
    public List<PostResponseDto> getLatestPostsFromSellers(UUID buyerId) {
        // Find the sellers that the user is following
        List<SellerResponseDto> sellers = buyerService.findFollowed(buyerId).getFollowed();

        // if the user is not following anyone, return not found
        if (sellers.isEmpty()) {
            throw new NotFoundException("The buyer is not following any sellers");
        }

        // Find posts from those sellers
        List<Post> posts = new ArrayList<>();
        sellers.forEach(seller ->
                posts.addAll(postRepository.findRecentPostsBySellerId(seller.getId()))
        );

        ObjectMapper mapper = new ObjectMapper();
        return posts.stream().map(post -> mapper.convertValue(post, PostResponseDto.class)).toList();
    }
}
