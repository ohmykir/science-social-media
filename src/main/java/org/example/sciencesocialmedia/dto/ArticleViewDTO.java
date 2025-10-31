package org.example.sciencesocialmedia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.sciencesocialmedia.entity.Article;

@Data
@AllArgsConstructor
public class ArticleViewDTO {
    private Article article;
    private int likeCount;
    private int commentCount;
}
