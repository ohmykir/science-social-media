package org.example.sciencesocialmedia.service;

import lombok.AllArgsConstructor;
import org.example.sciencesocialmedia.entity.Comment;
import org.example.sciencesocialmedia.repository.CommentRepository;
import org.example.sciencesocialmedia.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void addComment(String articleId, String username, String commentText) {
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setCommentatorId(userRepository.findByUsername(username).getId());
        comment.setCommentatorUsername(username);
        comment.setText(commentText);
        comment.setDate(LocalDateTime.now());

        commentRepository.save(comment);
    }

    @Transactional
    public List<Comment> getArticleComments(String articleId) {
        return commentRepository.getAllByArticleId(articleId);
    }
}
