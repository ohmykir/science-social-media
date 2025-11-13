package org.example.sciencesocialmedia.controller;

import lombok.AllArgsConstructor;
import org.example.sciencesocialmedia.dto.ImportBibtexResponse;
import org.example.sciencesocialmedia.service.ArticleImportService;
import org.example.sciencesocialmedia.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/import")
@AllArgsConstructor
public class ImportController {

    private final ArticleImportService articleImportService;
    private final UserService userService;

    @PostMapping("/bibtex")
    public String importBibtex(@RequestParam MultipartFile file,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Файл пустой");
                return "redirect:/user?id=" + userService.loadUserByUsername(principal.getName()).getId();
            }

            String filename = file.getOriginalFilename();
            if (!filename.endsWith(".txt") && !filename.endsWith(".bibtex") && !filename.endsWith(".bib")) {
                redirectAttributes.addFlashAttribute("error", "Поддерживаются только .txt, .bibtex и .bib файлы");
                return "redirect:/user?id=" + userService.loadUserByUsername(principal.getName()).getId();
            }

            ImportBibtexResponse response = articleImportService.importArticlesFromBibtex(file, principal);

            redirectAttributes.addFlashAttribute("success",
                    String.format("Успешно импортировано %d статей", response.getSuccessCount()));

            if (!response.getErrors().isEmpty()) {
                redirectAttributes.addFlashAttribute("warnings", response.getErrors());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка импорта: " + e.getMessage());
        }

        String userId = userService.loadUserByUsername(principal.getName()).getId();
        return "redirect:/user?id=" + userId;
    }

    @PostMapping("/bibtex-text")
    public String importBibtexFromText(@RequestParam String bibtexContent,
                                       Principal principal,
                                       RedirectAttributes redirectAttributes) {
        try {
            if (bibtexContent == null || bibtexContent.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Пожалуйста, введите BibTeX текст");
                return "redirect:/user?id=" + userService.loadUserByUsername(principal.getName()).getId();
            }

            ImportBibtexResponse response = articleImportService.importArticlesFromText(bibtexContent, principal);

            redirectAttributes.addFlashAttribute("success",
                    String.format("Успешно импортировано %d статей", response.getSuccessCount()));

            if (!response.getErrors().isEmpty()) {
                redirectAttributes.addFlashAttribute("warnings", response.getErrors());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка импорта: " + e.getMessage());
        }

        String userId = userService.loadUserByUsername(principal.getName()).getId();
        return "redirect:/user?id=" + userId;
    }
}