package com.example.intuitcraftdemoproject.service;

import com.example.intuitcraftdemoproject.dto.CommentRequest;
import com.example.intuitcraftdemoproject.enums.InteractionType;
import com.example.intuitcraftdemoproject.exception.ResourceNotFoundException;
import com.example.intuitcraftdemoproject.model.*;
import com.example.intuitcraftdemoproject.repository.CommentRepository;
import com.example.intuitcraftdemoproject.repository.DislikeRepository;
import com.example.intuitcraftdemoproject.repository.LikeRepository;
import com.example.intuitcraftdemoproject.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private DislikeRepository dislikeRepository;

    @Autowired
    private UserRepository userRepository;

    public Comments addComment(CommentRequest commentRequest) {
        Long postId = commentRequest.getPostId();
        Long parentCommentId = commentRequest.getParentCommentId();
        Long userId = commentRequest.getUserId();

        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        if (postId == null && parentCommentId == null) {
            throw new IllegalArgumentException("Either Post ID or Parent Comment ID must be provided");
        }

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

        Comments newComment = new Comments();
        newComment.setUser(user);
        newComment.setContent(commentRequest.getContent());

        if (parentCommentId != null) {
            Comments parentComment = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new RuntimeException("Parent comment with ID " + parentCommentId + " not found"));
            newComment.setParentCommentId(parentComment);
            newComment.setPostId(parentComment.getPostId()); // Derive postId from parent comment
            log.info("Adding reply to comment ID: {}", parentCommentId);
        } else {
            newComment.setPostId(postId);
            log.info("Adding comment to post ID: {}", postId);
        }

        Comments savedComment = commentRepository.save(newComment);
        log.info("Comment added with ID: {}", savedComment.getId());
        return savedComment;
    }


    public List<Comments> getRepliesByParentCommentId(Long commentId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Comments> replies = commentRepository.findRepliesByParentCommentId(commentId, pageable);
        log.info("Fetched {} replies for comment ID: {}", replies.size(), commentId);
        return replies;
    }

    @Transactional
    public Comments likeComment(Long commentId, Long userId) {
        return handleCommentInteraction(commentId, userId, InteractionType.LIKE);
    }

    @Transactional //Use for methods that need to perform write operations.
    // Supports both read and write operations within a single transaction.
    public Comments dislikeComment(Long commentId, Long userId) {
        return handleCommentInteraction(commentId, userId, InteractionType.DISLIKE);
    }

    private Comments handleCommentInteraction(Long commentId, Long userId, InteractionType interactionType) {
        Comments comment = getComment(commentId);
        Users user = getUser(userId);
        CommentInteractionId interactionId = new CommentInteractionId(commentId, userId);

        if (interactionType == InteractionType.LIKE) {
            handleLikeInteraction(comment, user, interactionId);
        } else if (interactionType == InteractionType.DISLIKE) {
            handleDislikeInteraction(comment, user, interactionId);
        }

        comment.setUpdatedAt(LocalDateTime.now());
        Comments updatedComment = commentRepository.save(comment);
        log.info("Updated comment with ID: {}", updatedComment.getId());
        return updatedComment;
    }

    private void handleLikeInteraction(Comments comment, Users user, CommentInteractionId interactionId) {
        if (likeRepository.existsById(interactionId)) {
            likeRepository.deleteById(interactionId);
            comment.setLikesCount(comment.getLikesCount() - 1);
            comment.getLikes().removeIf(like -> like.getId().equals(interactionId));
            log.info("Like removed for comment ID: {} by user ID: {}", comment.getId(), user.getId());
        } else {
            if (dislikeRepository.existsById(interactionId)) {
                dislikeRepository.deleteById(interactionId);
                comment.setDislikesCount(comment.getDislikesCount() - 1);
                comment.getDislikes().removeIf(dislike -> dislike.getId().equals(interactionId));
                log.info("Dislike removed for comment ID: {} by user ID: {}", comment.getId(), user.getId());
            }

            Likes like = new Likes();
            like.setId(interactionId);
            like.setUser(user);
            like.setComment(comment);
            likeRepository.save(like);
            comment.setLikesCount(comment.getLikesCount() + 1);
            comment.getLikes().add(like);
            log.info("Like added for comment ID: {} by user ID: {}", comment.getId(), user.getId());
        }
    }

    private void handleDislikeInteraction(Comments comment, Users user, CommentInteractionId interactionId) {
        if (dislikeRepository.existsById(interactionId)) {
            dislikeRepository.deleteById(interactionId);
            comment.setDislikesCount(comment.getDislikesCount() - 1);
            comment.getDislikes().removeIf(dislike -> dislike.getId().equals(interactionId));
            log.info("Dislike removed for comment ID: {} by user ID: {}", comment.getId(), user.getId());
        } else {
            if (likeRepository.existsById(interactionId)) {
                likeRepository.deleteById(interactionId);
                comment.setLikesCount(comment.getLikesCount() - 1);
                comment.getLikes().removeIf(like -> like.getId().equals(interactionId));
                log.info("Like removed for comment ID: {} by user ID: {}", comment.getId(), user.getId());
            }

            Dislikes dislike = new Dislikes();
            dislike.setId(interactionId);
            dislike.setComment(comment);
            dislike.setUser(user);
            dislikeRepository.save(dislike);
            comment.setDislikesCount(comment.getDislikesCount() + 1);
            comment.getDislikes().add(dislike);
            log.info("Dislike added for comment ID: {} by user ID: {}", comment.getId(), user.getId());
        }
    }

    private Comments getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with ID " + commentId + " not found"));
    }

    private Users getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
    }

    @Transactional(readOnly = true) //Use for methods that only perform read operations. Optimizes performance for
    // read-only transactions by preventing changes to the database and avoiding unnecessary flush operations.
    public List<Users> getCommentLikes(Long commentId) {
        List<Users> users = likeRepository.findByCommentId(commentId)
                .stream()
                .map(like -> userRepository.findById(like.getId().getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found")))
                .collect(Collectors.toList());
        log.info("Fetched {} likes for comment ID: {}", users.size(), commentId);
        return users;
    }

    @Transactional(readOnly = true)
    public List<Users> getCommentDislikes(Long commentId) {
        List<Users> users = dislikeRepository.findByCommentId(commentId)
                .stream()
                .map(dislike -> userRepository.findById(dislike.getId().getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found")))
                .collect(Collectors.toList());
        log.info("Fetched {} dislikes for comment ID: {}", users.size(), commentId);
        return users;
    }

    public List<Comments> getTopLevelComments(Long postId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Comments> comments = commentRepository.findTopNByPostIdAndParentCommentIdIsNull(postId, pageable);
        log.info("Fetched {} top-level comments for post ID: {}", comments.size(), postId);
        return comments;
    }

}
