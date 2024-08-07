package com.example.intuitcraftdemoproject.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Dislikes {

    @EmbeddedId
    private CommentInteractionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("commentId")
    @JoinColumn(name = "comment_id", insertable = false, updatable = false)
    private Comments comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Users user;

    private LocalDateTime createdAt;
}
