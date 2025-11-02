package org.example.sciencesocialmedia.entity.id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleTagId implements Serializable {
    private String articleId;
    private String tagId;
}