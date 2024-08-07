package com.example.intuitcraftdemoproject.service;

import com.example.intuitcraftdemoproject.dto.CommentRequest;
import com.example.intuitcraftdemoproject.exception.ResourceNotFoundException;
import com.example.intuitcraftdemoproject.model.*;
import com.example.intuitcraftdemoproject.repository.CommentRepository;
import com.example.intuitcraftdemoproject.repository.DislikeRepository;
import com.example.intuitcraftdemoproject.repository.LikeRepository;
import com.example.intuitcraftdemoproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
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

        if (commentRequest.getPostId() == null) {
            throw new IllegalArgumentException("Post ID must not be null");
        }
        Long userId = commentRequest.getUserId();
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

        Comments newComment = new Comments();
        newComment.setPostId(commentRequest.getPostId());
        newComment.setUser(user);
        newComment.setContent(commentRequest.getContent());
        Long parentCommentId = commentRequest.getParentCommentId();
        if (parentCommentId != null) {
            Comments parentComment = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new RuntimeException("Parent comment with ID " + parentCommentId + " not found"));
            newComment.setParentCommentId(parentComment);
        }
        return commentRepository.save(newComment);
    }


    public List<Comments> getRepliesByParentCommentId(Long commentId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return commentRepository.findRepliesByParentCommentId(commentId, pageable);
    }

    @Transactional
    public Comments likeComment(Long commentId, Long userId) {
        Comments comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with ID " + commentId+ "not found"));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
        CommentInteractionId commentInteractionId = new CommentInteractionId(commentId, userId);
        if (likeRepository.existsById(commentInteractionId)) {
            // Remove the like if it exists
            likeRepository.deleteById(commentInteractionId);
            comment.setLikesCount(comment.getLikesCount()-1);
            comment.getLikes().removeIf(like -> like.getId().equals(commentInteractionId));
        } else {
            // If user has disliked the same comment earlier remove the dislike from the comment
            if (dislikeRepository.existsById(commentInteractionId)) {
                dislikeRepository.deleteById(commentInteractionId);
                comment.setDislikesCount(comment.getDislikesCount() - 1);
                comment.getDislikes().removeIf(dislike -> dislike.getId().equals(commentInteractionId));
            }

            Likes likes = new Likes();
            likes.setId(commentInteractionId);
            likes.setUser(user);
            likes.setComment(comment);
            likeRepository.save(likes);
            comment.setLikesCount(comment.getLikesCount()+1);
            comment.getLikes().add(likes);
        }
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Transactional
    public Comments dislikeComment(Long commentId, Long userId) {
        Comments comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with ID " + commentId+ "not found"));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
        CommentInteractionId commentInteractionId = new CommentInteractionId(commentId, userId);

        // Check if the dislike already exists
        if (dislikeRepository.existsById(commentInteractionId)) {
            // Remove the dislike if it exists
            dislikeRepository.deleteById(commentInteractionId);
            comment.setDislikesCount(comment.getDislikesCount()-1);
            comment.getDislikes().removeIf(dislike -> dislike.getId().equals(commentInteractionId));
        } else {
            // If user has liked the same comment earlier remove the like from the comment
            if (likeRepository.existsById(commentInteractionId)) {
                likeRepository.deleteById(commentInteractionId);
                comment.setLikesCount(comment.getLikesCount() - 1);
                comment.getLikes().removeIf(like -> like.getId().equals(commentInteractionId));
            }

            // Add the dislike if it does not exist
            Dislikes dislike = new Dislikes();
            dislike.setId(commentInteractionId);
            dislike.setComment(comment);
            dislike.setUser(user); // Assuming User is set by userId
            dislikeRepository.save(dislike);
            comment.setDislikesCount(comment.getDislikesCount()+1);
            comment.getDislikes().add(dislike);
        }
        comment.setUpdatedAt(LocalDateTime.now());
        // Save the updated comment
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<Users> getCommentLikes(Long commentId) {
        return likeRepository.findByCommentId(commentId)
                .stream()
                .map(like -> userRepository.findById(like.getId().getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found")))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Users> getCommentDislikes(Long commentId) {
        return dislikeRepository.findByCommentId(commentId)
                .stream()
                .map(dislike -> userRepository.findById(dislike.getId().getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found")))
                .collect(Collectors.toList());
    }

    public List<Comments> getTopLevelComments(Long postId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return commentRepository.findTopNByPostIdAndParentCommentIdIsNull(postId, pageable);
    }

}
