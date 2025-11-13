package org.example.sciencesocialmedia.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.sciencesocialmedia.dto.ArticleDetailDTO;
import org.example.sciencesocialmedia.entity.Article;
import org.example.sciencesocialmedia.entity.User;
import org.example.sciencesocialmedia.repository.ArticleRepository;
import org.example.sciencesocialmedia.service.ArticleService;
import org.example.sciencesocialmedia.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping
public class ArticleController {

    private final ArticleService articleService;
    private final UserService userService;
    private final ArticleRepository articleRepository;

    @GetMapping("/article")
    public String getArticle(@RequestParam String id, Model model, Principal principal) {
        User current = userService.loadUserByUsername(principal.getName());

        ArticleDetailDTO article = articleService.getArticleById(id, current.getId());

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        boolean isAuthenticated = (auth != null) && (auth.isAuthenticated());

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("article", article);
        model.addAttribute("currentUserId", current.getId());
        return "article";
    }

    @GetMapping("/api/articles/{id}/pdf-data")
    @ResponseBody
    public Map<String, String> getPdfData(@PathVariable String id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        return Map.of(
                "pdfBase64", article.getPdfBase64() != null ? article.getPdfBase64() : "",
                "title", article.getTitle()
        );
    }


    @PostMapping("/article/{id}/upload-pdf")
    public String uploadPdfContent(@PathVariable String id,
                                   @RequestParam("file") MultipartFile file,
                                   Model model,
                                   Principal principal, RedirectAttributes redirectAttributes)
    {
        try {
            User user = userService.loadUserByUsername(principal.getName());
            articleService.uploadPdfContent(id, file, user.getId());
            redirectAttributes.addFlashAttribute("success", "Контент загружен из PDF");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка загрузки PDF: " + e.getMessage());
        }
        return "redirect:/article?id=" + id;
    }

    @PostMapping("/article/{id}/delete")
    public String deleteArticle(@PathVariable String id,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = userService.loadUserByUsername(principal.getName());
            articleService.deleteArticle(id, user.getId());
            redirectAttributes.addFlashAttribute("success", "Статья удалена");
            return "redirect:/user?id=" + user.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
            return "redirect:/article?id=" + id;
        }
    }
}