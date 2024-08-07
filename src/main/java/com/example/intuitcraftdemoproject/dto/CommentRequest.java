package com.example.intuitcraftdemoproject.dto;

import lombok.Data;

@Data
public class CommentRequest {

    private Long postId;
    private String content;
    private Long userId;
    private Long parentCommentId;
}
