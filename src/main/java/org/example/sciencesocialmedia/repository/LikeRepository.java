package org.example.sciencesocialmedia.repository;

import org.example.sciencesocialmedia.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, String> {

    @Query("select l from article_likes l where (l.articleId = :aId and l.userId = :uId)")
    Optional<Like> findByArticleIdAndUserId(@Param("aId") String articleId, @Param("uId") String userId);

    int countByArticleId(String articleId);
}
