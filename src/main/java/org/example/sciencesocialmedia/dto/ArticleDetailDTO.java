package org.example.sciencesocialmedia.dto;

import lombok.Data;
import org.example.sciencesocialmedia.entity.Article;

import java.util.List;

@Data
public class ArticleDetailDTO {
    private Article article;
    private int likeCount;
    private boolean likedByCurrentUser;
    private List<CommentDTO> comments;
}
