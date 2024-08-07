package com.example.intuitcraftdemoproject.controller;

import com.example.intuitcraftdemoproject.model.Posts;
import com.example.intuitcraftdemoproject.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public Posts addPost(@RequestBody String content) {
        return postService.addPost(content);
    }

    @GetMapping
    public List<Posts> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public Posts getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }
}
