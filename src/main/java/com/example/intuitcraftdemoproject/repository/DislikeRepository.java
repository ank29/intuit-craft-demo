package com.example.intuitcraftdemoproject.repository;

import com.example.intuitcraftdemoproject.model.CommentInteractionId;
import com.example.intuitcraftdemoproject.model.Dislikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DislikeRepository extends JpaRepository<Dislikes, CommentInteractionId> {
    List<Dislikes> findByCommentId(Long commentId);
}
