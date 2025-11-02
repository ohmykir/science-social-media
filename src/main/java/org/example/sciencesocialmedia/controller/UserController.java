package org.example.sciencesocialmedia.controller;

import lombok.AllArgsConstructor;
import org.example.sciencesocialmedia.dto.UserProfileDTO;
import org.example.sciencesocialmedia.dto.UserUpdateDTO;
import org.example.sciencesocialmedia.entity.Article;
import org.example.sciencesocialmedia.entity.Subscription;
import org.example.sciencesocialmedia.entity.User;
import org.example.sciencesocialmedia.service.ArticleService;
import org.example.sciencesocialmedia.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/user")
    public String getProfile(@RequestParam String id,
                             @RequestParam(defaultValue = "date") String sort,
                             Model model,
                             Principal principal)
    {
        String currentUserId = principal != null ? userService.loadUserByUsername(principal.getName()).getId() : null;

        UserProfileDTO userProfile = userService.getUserProfile(id, currentUserId, sort);
        boolean isOwner = currentUserId != null && currentUserId.equals(id);

        model.addAttribute("userProfile", userProfile);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("currentSort", sort);

        return "user";
    }

    @GetMapping("/user/edit")
    public String editProfile(Model model, Principal principal) {
        User user = userService.loadUserByUsername(principal.getName());
        model.addAttribute("user", user);
        return "user-edit";
    }

    @PostMapping("/user/edit")
    public String updateProfile(@ModelAttribute UserUpdateDTO updateDTO,
                                @RequestParam(required = false) MultipartFile photo,
                                Principal principal,
                                RedirectAttributes redirectAttributes)
    {
        try {
            User user = userService.loadUserByUsername(principal.getName());
            userService.updateProfile(user.getId(), updateDTO, photo);
            redirectAttributes.addFlashAttribute("success", "Профиль обновлен");
            return "redirect:/user?id=" + user.getId();
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
            return "redirect:/user/edit";
        }
    }

    @GetMapping("/user/{id}/subscribers")
    @ResponseBody
    public List<UserProfileDTO> getSubscribers(@PathVariable String id) {
        return userService.getSubscribers(id).stream()
                .map(this::mapToSimpleProfile)
                .toList();
    }

    @GetMapping("/user/{id}/subscriptions")
    @ResponseBody
    public List<UserProfileDTO> getSubscriptions(@PathVariable String id) {
        return userService.getSubscriptions(id).stream()
                .map(this::mapToSimpleProfile)
                .toList();
    }

    private UserProfileDTO mapToSimpleProfile(User user) {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setUser(user);
        return userProfileDTO;
    }
}
