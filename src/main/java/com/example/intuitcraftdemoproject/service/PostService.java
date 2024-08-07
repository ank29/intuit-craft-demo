package com.example.intuitcraftdemoproject.service;

import com.example.intuitcraftdemoproject.model.Posts;
import com.example.intuitcraftdemoproject.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Posts addPost(String content) {
        Posts posts = new Posts();
        posts.setContent(content);
        return postRepository.save(posts);
    }

    public List<Posts> getAllPosts() {
        return postRepository.findAll();
    }

    public Posts getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }
}
