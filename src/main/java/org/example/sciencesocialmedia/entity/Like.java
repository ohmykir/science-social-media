package org.example.sciencesocialmedia.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.example.sciencesocialmedia.entity.id.LikeId;

@Entity(name = "article_likes")
@Data
@IdClass(LikeId.class)
public class Like {
    @Id
    private String userId;
    @Id
    private String articleId;

    @ManyToOne
    @JoinColumn(name = "articleId", insertable = false, updatable = false)
    @ToString.Exclude
    private Article article;

    @ManyToOne
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    @ToString.Exclude
    private User user;

}
