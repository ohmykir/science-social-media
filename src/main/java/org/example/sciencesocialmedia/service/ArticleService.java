package org.example.sciencesocialmedia.service;

import lombok.AllArgsConstructor;
import org.example.sciencesocialmedia.dto.ArticleViewDTO;
import org.example.sciencesocialmedia.entity.Article;
import org.example.sciencesocialmedia.entity.User;
import org.example.sciencesocialmedia.repository.ArticleRepository;
import org.example.sciencesocialmedia.repository.CommentRepository;
import org.example.sciencesocialmedia.repository.LikeRepository;
import org.example.sciencesocialmedia.repository.UserRepository;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXParser;
import org.jbibtex.ParseException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public List<Article> getAllArticlesByAuthorId(String userId) {
        List<Article> articles = articleRepository.findAllByAuthorIdWithLikesAndComments(userId);
        return articles;
    }

    @Transactional
    public Article getArticleById(String articleId) {
        Article article = articleRepository.findByIdWithLikesAndComments(articleId);
        return article;
    }

    @Transactional
    public List<ArticleViewDTO> getTopLikedArticles(int limit) {
        List<Article> articles = articleRepository.findTopByOrderByLikesDesc(PageRequest.of(0, limit));
        List<ArticleViewDTO> dtos = new ArrayList<>();
        for (Article article : articles) {
            int likes = likeRepository.countByArticleId(article.getId());
            int comments = commentRepository.countByArticleId(article.getId());
            dtos.add(new ArticleViewDTO(article, likes, comments));
        }

        return dtos;
    }

    @Transactional
    public List<ArticleViewDTO> getTopCommentedArticles(int limit) {
        List<Article> articles = articleRepository.findTopByOrderByCommentsDesc(PageRequest.of(0, limit));
        List<ArticleViewDTO> dtos = new ArrayList<>();
        for (Article article : articles) {
            int likes = likeRepository.countByArticleId(article.getId());
            int comments = commentRepository.countByArticleId(article.getId());
            dtos.add(new ArticleViewDTO(article, likes, comments));
        }

        return dtos;
    }


    public void importArticlesFromBibtex(MultipartFile file, Principal principal) throws IOException, ParseException {
        Reader reader = new InputStreamReader(file.getInputStream());
        BibTeXParser parser = new BibTeXParser();
        BibTeXDatabase database = parser.parse(reader);

        User user = userRepository.findByUsername(principal.getName());

        for (BibTeXEntry entry : database.getEntries().values()) {
            Article article = new Article();

            if (entry.getField(BibTeXEntry.KEY_TITLE) != null) {
                article.setTitle(entry.getField(BibTeXEntry.KEY_TITLE).toUserString());
            }
            if (entry.getField(BibTeXEntry.KEY_AUTHOR) != null) {
                article.setAuthor(entry.getField(BibTeXEntry.KEY_AUTHOR).toUserString());
            }
            if (entry.getField(BibTeXEntry.KEY_JOURNAL) != null) {
                article.setJournal(entry.getField(BibTeXEntry.KEY_JOURNAL).toUserString());
            }
            if (entry.getField(BibTeXEntry.KEY_NUMBER) != null) {
                article.setNumber(Integer.valueOf(entry.getField(BibTeXEntry.KEY_NUMBER).toUserString()));
            }
            if (entry.getField(BibTeXEntry.KEY_PAGES) != null) {
                article.setPages(entry.getField(BibTeXEntry.KEY_PAGES).toUserString());
            }
            if (entry.getField(BibTeXEntry.KEY_PUBLISHER) != null) {
                article.setPublisher(entry.getField(BibTeXEntry.KEY_PUBLISHER).toUserString());
            }
            if (entry.getField(BibTeXEntry.KEY_YEAR) != null) {
                article.setYear(Integer.valueOf(entry.getField(BibTeXEntry.KEY_YEAR).toUserString()));
            }

            article.setAuthorId(user.getId());
            articleRepository.save(article);
        }
    }
}
