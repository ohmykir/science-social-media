package org.example.sciencesocialmedia.service;

import lombok.AllArgsConstructor;
import org.example.sciencesocialmedia.entity.Like;
import org.example.sciencesocialmedia.repository.LikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    @Transactional
    public boolean toggleLike(String articleId, String userId) {
        Optional<Like> existing = likeRepository.findByArticleIdAndUserId(articleId, userId);

        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            return false;
        } else {
            Like like = new Like();
            like.setArticleId(articleId);
            like.setUserId(userId);
            likeRepository.save(like);
            return true;
        }
    }

    @Transactional
    public boolean userLiked(String articleId, String userId) {
        return likeRepository.findByArticleIdAndUserId(articleId, userId).isPresent();
    }

    @Transactional
    public int getLikeCount(String articleId) {
        return likeRepository.countByArticleId(articleId);
    }
}
