package org.example.sciencesocialmedia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private String id;
    private String articleId;
    private String commentatorId;
    private String commentatorUsername;
    private String text;
    private LocalDateTime createdAt;
}