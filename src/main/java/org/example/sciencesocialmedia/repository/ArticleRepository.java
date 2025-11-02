package org.example.sciencesocialmedia.repository;

import org.example.sciencesocialmedia.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {

    @Query("select a from articles a where a.authorId = :authorId order by a.published desc")
    List<Article> findByAuthorIdOrderByPublishedDesc(String authorId);

    @Query("select a from articles a order by a.published desc")
    Page<Article> findAllOrderByPublishedDesc(PageRequest request);

    boolean existsByAuthorIdAndTitle(String authorId, String title);

    List<Article> findByAuthorIdOrderByTitleAsc(String authorId);

    List<Article> findByAuthorIdOrderByYearDesc(String authorId);
}