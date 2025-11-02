package org.example.sciencesocialmedia.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.sciencesocialmedia.entity.Comment;
import org.example.sciencesocialmedia.entity.User;
import org.example.sciencesocialmedia.repository.ArticleRepository;
import org.example.sciencesocialmedia.repository.CommentRepository;
import org.example.sciencesocialmedia.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public void addComment(String articleId, String username, String text) {
        articleRepository.findById(articleId)
                .orElseThrow(EntityNotFoundException::new);

        User commentator = userRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException::new);

        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setCommentatorId(commentator.getId());
        comment.setText(text);
        comment.setCreatedAt(LocalDateTime.now());

        commentRepository.save(comment);
    }
}