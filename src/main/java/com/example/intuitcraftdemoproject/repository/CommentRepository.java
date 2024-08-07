package com.example.intuitcraftdemoproject.repository;

import com.example.intuitcraftdemoproject.model.Comments;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comments, Long> {

    // Fetch replies for a given comment
    @Query("SELECT c FROM Comments c WHERE c.parentCommentId.id = :parentCommentId ORDER BY c.createdAt ASC")
    List<Comments> findRepliesByParentCommentId(@Param("parentCommentId") Long parentCommentId, Pageable pageable);

    @Query(value = "SELECT c FROM Comments c WHERE c.postId = :postId AND c.parentCommentId IS NULL ORDER BY c.createdAt DESC")
    List<Comments> findTopNByPostIdAndParentCommentIdIsNull(@Param("postId") Long postId, Pageable pageable);}
