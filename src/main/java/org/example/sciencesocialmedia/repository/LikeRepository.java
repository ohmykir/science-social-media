package org.example.sciencesocialmedia.repository;

import org.example.sciencesocialmedia.entity.Like;
import org.example.sciencesocialmedia.entity.id.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public interface LikeRepository extends JpaRepository<Like, LikeId> {

    @Query("select count(l) from article_likes l where l.articleId = :articleId")
    int countByArticleId(String articleId);

    @Query("select cast(l.articleId as java.lang.String), count(l) from article_likes l where l.articleId in :articleIds group by l.articleId")
    List<Object[]> countByArticleIdIn(List<String> articleIds);

    @Query("select l.articleId from article_likes l where l.userId = :userId and l.articleId IN :articleIds")
    Set<String> findArticleIdsLikedByUser(String userId, List<String> articleIds);

    boolean existsByUserIdAndArticleId(String userId, String articleId);

    void deleteByArticleId(String articleId);
}