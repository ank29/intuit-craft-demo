package com.example.intuitcraftdemoproject.controller;

import com.example.intuitcraftdemoproject.dto.CommentRequest;
import com.example.intuitcraftdemoproject.model.Comments;
import com.example.intuitcraftdemoproject.model.Users;
import com.example.intuitcraftdemoproject.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@Slf4j
public class CommentController {


    @Autowired
    private CommentService commentService;

    @PostMapping
    public Comments addComment(@RequestBody CommentRequest commentRequest) {
        log.info("Add comment request received for postId {}  ", commentRequest.getPostId());
        return commentService.addComment(commentRequest);
    }

    @GetMapping("/post/top/{postId}")
    public ResponseEntity<List<Comments>> getTopLevelComments(@PathVariable Long postId, @RequestParam(defaultValue = "10") int limit) {
        log.info("Get Top {} LevelComments for postId {}  ", limit, postId);
        List<Comments> commentsList = commentService.getTopLevelComments(postId, limit);
        return ResponseEntity.ok(commentsList);
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<Comments>> getRepliesByParentCommentId(@PathVariable Long commentId,  @RequestParam(defaultValue = "10") int limit) {
        log.info("Get Replies for commentId {} ", commentId);
        List<Comments> commentsList = commentService.getRepliesByParentCommentId(commentId, limit);
        return ResponseEntity.ok(commentsList);
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<Comments> likeComment(@PathVariable Long commentId, @RequestParam Long userId) {
        Comments comment = commentService.likeComment(commentId, userId);
        return ResponseEntity.ok(comment);
    }

    @PostMapping("/{commentId}/dislike")
    public ResponseEntity<Comments> dislikeComment(@PathVariable Long commentId, @RequestParam Long userId) {
        Comments comment = commentService.dislikeComment(commentId, userId);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/{commentId}/likes")
    public ResponseEntity<List<Users>> getCommentLikes(@PathVariable Long commentId) {
       List<Users> usersList = commentService.getCommentLikes(commentId);
        return ResponseEntity.ok(usersList);
    }

    @GetMapping("/{commentId}/dislikes")
    public ResponseEntity<List<Users>> getCommentDislikes(@PathVariable Long commentId) {
        List<Users> usersList = commentService.getCommentDislikes(commentId);
        return ResponseEntity.ok(usersList);
    }
}
