package org.example.sciencesocialmedia.controller;

import lombok.AllArgsConstructor;
import org.example.sciencesocialmedia.dto.ArticleViewDTO;
import org.example.sciencesocialmedia.entity.User;
import org.example.sciencesocialmedia.service.ArticleService;
import org.example.sciencesocialmedia.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
public class HomeController {

    private UserService userService;
    private ArticleService articleService;

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        List<ArticleViewDTO> topLikedArticles = articleService.getTopLikedArticles(6);
        List<ArticleViewDTO> topCommentedArticles = articleService.getTopCommentedArticles(6);

        if (principal != null) {
            User currentUser = userService.loadUserByUsername(principal.getName());
            model.addAttribute("currentUserId", currentUser.getId());
        }

        model.addAttribute("topLikedArticles", topLikedArticles);
        model.addAttribute("topCommentedArticles", topCommentedArticles);

        return "home";
    }
}

