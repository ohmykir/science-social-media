package org.example.sciencesocialmedia.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.example.sciencesocialmedia.dto.ArticleDetailDTO;
import org.example.sciencesocialmedia.dto.ArticleViewDTO;
import org.example.sciencesocialmedia.dto.CommentDTO;
import org.example.sciencesocialmedia.entity.Article;
import org.example.sciencesocialmedia.entity.Comment;
import org.example.sciencesocialmedia.entity.Like;
import org.example.sciencesocialmedia.entity.User;
import org.example.sciencesocialmedia.entity.id.LikeId;
import org.example.sciencesocialmedia.repository.ArticleRepository;
import org.example.sciencesocialmedia.repository.CommentRepository;
import org.example.sciencesocialmedia.repository.LikeRepository;
import org.example.sciencesocialmedia.repository.UserRepository;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXParser;
import org.jbibtex.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@AllArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public List<ArticleViewDTO> mapToViewDTO(List<Article> articles) {
        List<String> articleIds = articles.stream().map(Article::getId).toList();
        Map<String, Integer> likeCounts = getLikeCounts(articleIds);
        Map<String, Integer> commentCounts = getCommentCounts(articleIds);

        return articles.stream()
                .map(a -> new ArticleViewDTO(
                        a,
                        likeCounts.getOrDefault(a.getId(), 0),
                        commentCounts.getOrDefault(a.getId(), 0)
                ))
                .toList();
    }

    @Transactional
    public List<ArticleViewDTO> getAllArticlesByAuthorId(String userId) {
        List<Article> articles = articleRepository.findByAuthorIdOrderByPublishedDesc(userId);

        return mapToViewDTO(articles);
    }

    @Transactional
    public ArticleDetailDTO getArticleById(String articleId, String userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(EntityNotFoundException::new);

        int likeCount = likeRepository.countByArticleId(articleId);
        boolean likedByCurrentUser = likeRepository.existsByUserIdAndArticleId(userId, articleId);

        List<CommentDTO> comments = getComments(articleId);

        ArticleDetailDTO dto = new ArticleDetailDTO();
        dto.setArticle(article);
        dto.setLikeCount(likeCount);
        dto.setLikedByCurrentUser(likedByCurrentUser);
        dto.setComments(comments);


        return dto;
    }

    @Transactional
    public void deleteArticle(String articleId, String userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        if (!article.getAuthorId().equals(userId)) {
            throw new RuntimeException("You can only delete your own articles");
        }

        likeRepository.deleteByArticleId(articleId);
        commentRepository.deleteByArticleId(articleId);

        articleRepository.deleteById(articleId);
    }

    @Transactional
    public List<ArticleViewDTO> getTopLikedArticles(int limit) {
        Page<Article> articles = articleRepository.findAllOrderByPublishedDesc(PageRequest.of(0, limit));

        List<String> articleIds = articles.stream().map(Article::getId).toList();
        Map<String, Integer> likeCounts = getLikeCounts(articleIds);
        Map<String, Integer> commentCounts = getCommentCounts(articleIds);

        return articles.stream()
                .map(a -> new ArticleViewDTO(
                        a,
                        likeCounts.getOrDefault(a.getId(), 0),
                        commentCounts.getOrDefault(a.getId(), 0)
                ))
                .sorted((a, b) -> Integer.compare(b.getLikeCount(), a.getLikeCount()))
                .limit(limit)
                .toList();
    }

    @Transactional
    public List<ArticleViewDTO> getTopCommentedArticles(int limit) {
        Page<Article> articles = articleRepository.findAllOrderByPublishedDesc(PageRequest.of(0, limit));

        List<String> articleIds = articles.stream().map(Article::getId).toList();
        Map<String, Integer> likeCounts = getLikeCounts(articleIds);
        Map<String, Integer> commentCounts = getCommentCounts(articleIds);

        return articles.stream()
                .map(a -> new ArticleViewDTO(
                        a,
                        likeCounts.getOrDefault(a.getId(), 0),
                        commentCounts.getOrDefault(a.getId(), 0)
                ))
                .sorted((a, b) -> Integer.compare(b.getCommentCount(), a.getCommentCount()))
                .limit(limit)
                .toList();
    }

    public List<Article> getArticlesSortedByLikes(String authorId) {
        List<Article> articles = articleRepository.findByAuthorIdOrderByPublishedDesc(authorId);
        Map<String, Integer> likeCounts = getLikeCounts(
                articles.stream().map(Article::getId).toList()
        );
        return articles.stream()
                .sorted((a, b) -> Integer.compare(
                        likeCounts.getOrDefault(b.getId(), 0),
                        likeCounts.getOrDefault(a.getId(), 0)
                ))
                .toList();
    }

    public List<Article> getArticlesSortedByComments(String authorId) {
        List<Article> articles = articleRepository.findByAuthorIdOrderByPublishedDesc(authorId);
        Map<String, Integer> commentCounts = getCommentCounts(
                articles.stream().map(Article::getId).toList()
        );
        return articles.stream()
                .sorted((a, b) -> Integer.compare(
                        commentCounts.getOrDefault(b.getId(), 0),
                        commentCounts.getOrDefault(a.getId(), 0)
                ))
                .toList();
    }

    @Transactional
    public void uploadPdfContent(String articleId, MultipartFile file, String userId) throws IOException {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(EntityNotFoundException::new);

        if (!article.getAuthorId().equals(userId)) {
            throw new RuntimeException();
        }

        String pdfContent = extractTextFromPdf(file);
        article.setContent(pdfContent);
        articleRepository.save(article);
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            String text = stripper.getText(document);

            text = text.replace("\r\n", "\n").replace("\r", "\n");

            StringBuilder html = new StringBuilder();
            html.append("<pre>");
            html.append(text);
            html.append("</pre>");

            return html.toString();
        }
    }

    private Map<String, Integer> getLikeCounts(List<String> articleIds) {
        if (articleIds.isEmpty()) return Map.of();

        return likeRepository.countByArticleIdIn(articleIds).stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> ((Number) row[1]).intValue()
                ));
    }

    private Map<String, Integer> getCommentCounts(List<String> articleIds) {
        if (articleIds.isEmpty()) return Map.of();

        return commentRepository.countByArticleIdIn(articleIds).stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> ((Number) row[1]).intValue()
                ));
    }

    private List<CommentDTO> getComments(String articleId) {
        List<Comment> comments = commentRepository.findByArticleIdOrderByCreatedAtDesc(articleId);

        Set<String> commentatorIds = comments.stream()
                .map(Comment::getCommentatorId)
                .collect(Collectors.toSet());

        Map<String, String> commentatorNames = userRepository.findAllById(commentatorIds).stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));

        return comments.stream()
                .map(c -> mapToCommentDTO(c, commentatorNames.get(c.getCommentatorId())))
                .toList();
    }

    private CommentDTO mapToCommentDTO(Comment comment, String commentatorName) {
        return new CommentDTO(
                comment.getId(),
                comment.getArticleId(),
                comment.getCommentatorId(),
                commentatorName,
                comment.getText(),
                comment.getCreatedAt()
        );
    }
}
