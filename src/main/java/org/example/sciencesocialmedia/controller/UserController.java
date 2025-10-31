package org.example.sciencesocialmedia.controller;

import lombok.AllArgsConstructor;
import org.example.sciencesocialmedia.entity.Article;
import org.example.sciencesocialmedia.entity.User;
import org.example.sciencesocialmedia.service.ArticleService;
import org.example.sciencesocialmedia.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final ArticleService articleService;

    @GetMapping("/user")
    public String getProfile(@RequestParam String id, Model model, Principal principal) {
        User user = userService.findByIdWithSubscriptions(id);
        List<Article> userArticles = articleService.getAllArticlesByAuthorId(id);

        boolean isOwner = principal != null && principal.getName().equals(user.getUsername());

        model.addAttribute("user", user);
        model.addAttribute("userArticles", userArticles);
        model.addAttribute("isOwner", isOwner);
        return "user";
    }
}
