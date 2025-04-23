package com.mercadolibre.be_java_hisp_w31_g07.repository.implementations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mercadolibre.be_java_hisp_w31_g07.model.Post;
import com.mercadolibre.be_java_hisp_w31_g07.repository.IPostRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Repository
public class PostRepository implements IPostRepository {
    private List<Post> postList = new ArrayList<>();

    public PostRepository() throws IOException {
        loadDataBasePost();
    }

    private void loadDataBasePost() throws IOException {
        File file;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        List<Post> posts;

        file = ResourceUtils.getFile("classpath:post.json");
        posts = objectMapper.readValue(file, new TypeReference<List<Post>>() {
        });

        postList = posts;
    }

    @Override
    public void createPost(Post post) {
        if (post.getHasPromo() == null) {
            post.setHasPromo(false);
        }
        if (post.getDiscount() == null) {
            post.setDiscount(0.0);
        }
        postList.add(post);
    }

    @Override
    public Optional<Post> findById(UUID postId) {
        return postList.stream()
                .filter(post -> post.getId().equals(postId))
                .findFirst();
    }

    @Override
    public List<Post> findHasPromo(UUID userId) {
        return postList.stream()
                .filter(post -> post.getHasPromo().equals(true) && post.getSellerId().equals(userId))
                .toList();
    }

    public List<Post> findLatestPostsFromSellers(List<UUID> sellers) {
        LocalDate twoWeeksAgo = LocalDate.now().minusWeeks(2);

        return postList.stream()
                .filter(post -> sellers.contains(post.getSellerId())
                        && post.getDate().isAfter(twoWeeksAgo))
                .sorted(Comparator.comparing(Post::getDate).reversed())
                .toList();
    }
}
