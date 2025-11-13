package org.example.sciencesocialmedia.service;

import lombok.RequiredArgsConstructor;
import org.example.sciencesocialmedia.dto.ImportBibtexResponse;
import org.example.sciencesocialmedia.entity.Article;
import org.example.sciencesocialmedia.entity.User;
import org.example.sciencesocialmedia.repository.ArticleRepository;
import org.example.sciencesocialmedia.repository.UserRepository;
import org.jbibtex.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleImportService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private static final int BATCH_SIZE = 50;

    public ImportBibtexResponse importArticlesFromBibtex(MultipartFile file, Principal principal) throws IOException {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            return parseBibtexFromReader(reader, user.getId());
        } catch (org.jbibtex.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public ImportBibtexResponse importArticlesFromText(String bibtexContent, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (bibtexContent == null || bibtexContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Содержимое не может быть пустым");
        }

        try (Reader reader = new StringReader(bibtexContent)) {
            return parseBibtexFromReader(reader, user.getId());
        } catch (org.jbibtex.ParseException e) {
            throw new RuntimeException("Ошибка парсинга BibTeX: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ImportBibtexResponse parseBibtexFromReader(Reader reader, String userId) throws org.jbibtex.ParseException {
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failedCount = 0;

        BibTeXParser parser = new BibTeXParser();
        BibTeXDatabase database = parser.parse(reader);

        List<Article> articles = new ArrayList<>();

        for (BibTeXEntry entry : database.getEntries().values()) {
            try {
                Article article = parseArticleFromEntry(entry, userId);
                articles.add(article);
                successCount++;

                if (articles.size() >= BATCH_SIZE) {
                    articleRepository.saveAll(articles);
                    articles.clear();
                }
            } catch (Exception e) {
                failedCount++;
                String errorMsg = String.format("Entry '%s': %s", entry.getKey(), e.getMessage());
                errors.add(errorMsg);
            }
        }

        if (!articles.isEmpty()) {
            articleRepository.saveAll(articles);
        }

        return new ImportBibtexResponse(successCount, failedCount, errors);
    }

    private Article parseArticleFromEntry(BibTeXEntry entry, String authorId) {
        Article article = new Article();
        article.setAuthorId(authorId);
        article.setPublished(LocalDateTime.now());

        String title = getFieldAsString(entry, BibTeXEntry.KEY_TITLE);
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Название обязательно");
        }

        if (articleRepository.existsByAuthorIdAndTitle(authorId, title)) {
            throw new IllegalArgumentException("Статья с этим названием уже есть");
        }

        article.setTitle(title);
        article.setAuthor(getFieldAsString(entry, BibTeXEntry.KEY_AUTHOR));
        article.setJournal(getFieldAsString(entry, BibTeXEntry.KEY_JOURNAL));
        article.setNumber(getFieldAsInteger(entry, BibTeXEntry.KEY_NUMBER));
        article.setPages(getFieldAsString(entry, BibTeXEntry.KEY_PAGES));
        article.setPublisher(getFieldAsString(entry, BibTeXEntry.KEY_PUBLISHER));
        article.setYear(getFieldAsInteger(entry, BibTeXEntry.KEY_YEAR));

        return article;
    }

    private String getFieldAsString(BibTeXEntry entry, Key key) {
        Value field = entry.getField(key);
        if (field != null) {
            String value = field.toUserString();
            return value.isBlank() ? null : value;
        }
        return null;
    }

    private Integer getFieldAsInteger(BibTeXEntry entry, Key key) {
        String value = getFieldAsString(entry, key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
