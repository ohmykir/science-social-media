package org.example.sciencesocialmedia.repository;

import org.example.sciencesocialmedia.entity.Article;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {
    @Query("select a from articles a left join fetch a.likes left join fetch a.comments where a.authorId = :id")
    List<Article> findAllByAuthorIdWithLikesAndComments(@Param("id")String authorId);

    @Query("select a from articles a left join fetch a.likes left join fetch a.comments where a.id = :id")
    Article findByIdWithLikesAndComments(@Param("id") String id);

    @Query("select a from articles a left join a.likes l group by a order by COUNT(l) desc")
    List<Article> findTopByOrderByLikesDesc(PageRequest pageRequest);

    @Query("select a from articles a left join a.comments c group by a order by COUNT(c) desc")
    List<Article> findTopByOrderByCommentsDesc(PageRequest pageRequest);
}