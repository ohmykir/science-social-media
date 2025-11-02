package org.example.sciencesocialmedia.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import org.example.sciencesocialmedia.entity.id.ArticleTagId;

@Entity(name = "article_tags")
@IdClass(ArticleTagId.class)
public class ArticleTag {
    @Id
    private String articleId;
    @Id
    private String tagId;
}
