package org.example.sciencesocialmedia.repository;

import org.example.sciencesocialmedia.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByArticleIdOrderByCreatedAtDesc(String articleId);

    @Query("select count(c) from comments c where c.articleId = :articleId")
    int countByArticleId(String articleId);

    @Query("select cast(c.articleId as java.lang.String), count(c) from comments c where c.articleId in :articleIds group by c.articleId")
    List<Object[]> countByArticleIdIn(List<String> articleIds);

    void deleteByArticleId(String articleId);
}