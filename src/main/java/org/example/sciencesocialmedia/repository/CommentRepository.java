package org.example.sciencesocialmedia.repository;

import org.example.sciencesocialmedia.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    int countByArticleId(String articleId);
    List<Comment> getAllByArticleId(String articleId);
}
