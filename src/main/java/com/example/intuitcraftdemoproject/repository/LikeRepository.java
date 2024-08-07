package com.example.intuitcraftdemoproject.repository;

import com.example.intuitcraftdemoproject.model.CommentInteractionId;
import com.example.intuitcraftdemoproject.model.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Likes, CommentInteractionId> {
    List<Likes> findByCommentId(Long commentId);
}
