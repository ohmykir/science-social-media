package org.example.sciencesocialmedia.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity(name = "comments")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private String id;
    private String articleId;
    private String commentatorId;
    private String commentatorUsername;
    private String text;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "articleId", insertable = false, updatable = false)
    private Article article;
}