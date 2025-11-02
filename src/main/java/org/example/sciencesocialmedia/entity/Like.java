package org.example.sciencesocialmedia.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.sciencesocialmedia.entity.id.LikeId;

@Entity(name = "article_likes")
@Data
@IdClass(LikeId.class)
public class Like {
    @Id
    private String userId;
    @Id
    private String articleId;
}