package com.example.intuitcraftdemoproject;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.intuitcraftdemoproject.dto.CommentRequest;
import com.example.intuitcraftdemoproject.exception.ResourceNotFoundException;
import com.example.intuitcraftdemoproject.model.*;
import com.example.intuitcraftdemoproject.repository.*;
import com.example.intuitcraftdemoproject.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private DislikeRepository dislikeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddComment_WithParentComment() {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setUserId(1L);
        commentRequest.setContent("This is a reply");
        commentRequest.setParentCommentId(1L);

        Users user = new Users();
        user.setId(1L);

        Comments parentComment = new Comments();
        parentComment.setId(1L);
        parentComment.setPostId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(parentComment));
        when(commentRepository.save(any(Comments.class))).thenAnswer(invocation -> {
            Comments comment = invocation.getArgument(0);
            comment.setId(2L); // Setting the ID for the saved comment
            return comment;
        });

        Comments result = commentService.addComment(commentRequest);

        assertNotNull(result);
        assertEquals(1L, result.getPostId());
        assertEquals(user, result.getUser());
        assertEquals("This is a reply", result.getContent());
        assertEquals(parentComment, result.getParentCommentId());
    }

    @Test
    void testAddComment_WithPostId() {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setPostId(1L);
        commentRequest.setUserId(1L);
        commentRequest.setContent("This is a comment");

        Users user = new Users();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comments.class))).thenAnswer(invocation -> {
            Comments comment = invocation.getArgument(0);
            comment.setId(1L); // Setting the ID for the saved comment
            return comment;
        });

        Comments result = commentService.addComment(commentRequest);

        assertNotNull(result);
        assertEquals(1L, result.getPostId());
        assertEquals(user, result.getUser());
        assertEquals("This is a comment", result.getContent());
        assertNull(result.getParentCommentId());
    }

    @Test
    void testAddComment_WithoutPostIdAndParentCommentId() {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setUserId(1L);
        commentRequest.setContent("This is a comment without postId and parentCommentId");

        Users user = new Users();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            commentService.addComment(commentRequest);
        });

        assertEquals("Either Post ID or Parent Comment ID must be provided", exception.getMessage());
    }


    @Test
    void testGetRepliesByParentCommentId() {
        Long parentCommentId = 1L;
        int limit = 10;
        Comments reply1 = new Comments();
        Comments reply2 = new Comments();
        List<Comments> replies = List.of(reply1, reply2);

        when(commentRepository.findRepliesByParentCommentId(eq(parentCommentId), any(Pageable.class))).thenReturn(replies);

        List<Comments> result = commentService.getRepliesByParentCommentId(parentCommentId, limit);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testLikeComment_AddLike() {
        Long commentId = 1L;
        Long userId = 1L;

        Comments comment = new Comments();
        comment.setId(commentId);
        comment.setLikesCount(0);
        Users user = new Users();
        user.setId(userId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(likeRepository.existsById(any(CommentInteractionId.class))).thenReturn(false);
        when(dislikeRepository.existsById(any(CommentInteractionId.class))).thenReturn(false);
        when(commentRepository.save(any(Comments.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comments result = commentService.likeComment(commentId, userId);

        assertNotNull(result);
        assertEquals(1, result.getLikesCount());
    }

    @Test
    void testDislikeComment_AddDislike() {
        Long commentId = 1L;
        Long userId = 1L;

        Comments comment = new Comments();
        comment.setId(commentId);
        comment.setDislikesCount(0);
        Users user = new Users();
        user.setId(userId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(dislikeRepository.existsById(any(CommentInteractionId.class))).thenReturn(false);
        when(likeRepository.existsById(any(CommentInteractionId.class))).thenReturn(false);
        when(commentRepository.save(any(Comments.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comments result = commentService.dislikeComment(commentId, userId);

        assertNotNull(result);
        assertEquals(1, result.getDislikesCount());
    }

    @Test
    void testGetCommentLikes() {
        Long commentId = 1L;
        Likes like = new Likes();
        CommentInteractionId commentInteractionId = new CommentInteractionId(commentId, 1L);
        like.setId(commentInteractionId);

        when(likeRepository.findByCommentId(commentId)).thenReturn(List.of(like));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new Users()));

        List<Users> result = commentService.getCommentLikes(commentId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetCommentDislikes() {
        Long commentId = 1L;
        Dislikes dislike = new Dislikes();
        CommentInteractionId commentInteractionId = new CommentInteractionId(commentId, 1L);
        dislike.setId(commentInteractionId);

        when(dislikeRepository.findByCommentId(commentId)).thenReturn(List.of(dislike));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new Users()));

        List<Users> result = commentService.getCommentDislikes(commentId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetTopLevelComments() {
        Long postId = 1L;
        int limit = 10;
        Comments comment1 = new Comments();
        Comments comment2 = new Comments();
        List<Comments> comments = List.of(comment1, comment2);

        when(commentRepository.findTopNByPostIdAndParentCommentIdIsNull(eq(postId), any(Pageable.class))).thenReturn(comments);

        List<Comments> result = commentService.getTopLevelComments(postId, limit);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testLikeComment_CommentNotFound() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.likeComment(1L, 1L);
        });
    }

    @Test
    public void testLikeComment_UserNotFound() {
        Comments comment = new Comments();
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.likeComment(1L, 1L);
        });
    }

    @Test
    public void testDislikeComment_CommentNotFound() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.dislikeComment(1L, 1L);
        });
    }

    @Test
    public void testDislikeComment_UserNotFound() {
        Comments comment = new Comments();
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.dislikeComment(1L, 1L);
        });
    }

}