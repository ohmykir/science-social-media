package org.example.sciencesocialmedia.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.sciencesocialmedia.entity.Like;
import org.example.sciencesocialmedia.entity.id.LikeId;
import org.example.sciencesocialmedia.repository.ArticleRepository;
import org.example.sciencesocialmedia.repository.LikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public boolean toggleLike(String articleId, String userId) {
        articleRepository.findById(articleId)
                .orElseThrow(EntityNotFoundException::new);

        LikeId likeId = new LikeId(userId, articleId);

        if (likeRepository.existsById(likeId)) {
            likeRepository.deleteById(likeId);
            return false;
        } else {
            Like like = new Like();
            like.setUserId(userId);
            like.setArticleId(articleId);
            likeRepository.save(like);
            return true;
        }
    }

    public int getLikeCount(String articleId) {
        return likeRepository.countByArticleId(articleId);
    }
}