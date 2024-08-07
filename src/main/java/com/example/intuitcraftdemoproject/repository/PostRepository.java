package com.example.intuitcraftdemoproject.repository;

import com.example.intuitcraftdemoproject.model.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Posts, Long> {
}
