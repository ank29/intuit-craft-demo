package com.example.intuitcraftdemoproject.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class CommentInteractionId implements Serializable {

    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "user_id")
    private Long userId;

    public CommentInteractionId() {
    }

    public CommentInteractionId(Long commentId, Long userId) {
        this.commentId = commentId;
        this.userId = userId;
    }

}
