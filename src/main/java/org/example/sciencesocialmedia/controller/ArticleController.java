package org.example.sciencesocialmedia.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.example.sciencesocialmedia.entity.Article;
import org.example.sciencesocialmedia.entity.Comment;
import org.example.sciencesocialmedia.entity.User;
import org.example.sciencesocialmedia.service.ArticleService;
import org.example.sciencesocialmedia.service.CommentService;
import org.example.sciencesocialmedia.service.LikeService;
import org.example.sciencesocialmedia.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping
public class ArticleController {

    private final LikeService likeService;
    private final ArticleService articleService;
    private final UserService userService;
    private final CommentService commentService;

    @GetMapping("/article")
    public String getArticle(@RequestParam String id, Model model, Principal principal) {
        User current = userService.loadUserByUsername(principal.getName());

        Article article = articleService.getArticleById(id);

        boolean liked = likeService.userLiked(article.getId(), current.getId());
        List<Comment> comments = commentService.getArticleComments(article.getId());

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        boolean isAuthenticated = (auth != null) && (auth.isAuthenticated());

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("userLiked", liked);
        model.addAttribute("article", article);
        model.addAttribute("comments", comments);
        model.addAttribute("currentUserId", current.getId());
        return "article";
    }

}