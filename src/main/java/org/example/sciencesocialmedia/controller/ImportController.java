package org.example.sciencesocialmedia.controller;

import lombok.AllArgsConstructor;
import org.example.sciencesocialmedia.service.ArticleService;
import org.jbibtex.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/api/import")
@AllArgsConstructor
public class ImportController {

    private ArticleService articleService;

    @PostMapping("/bibtex")
    public ResponseEntity<String> importBibtex(@RequestParam MultipartFile file, Principal principal) throws IOException, ParseException {
        articleService.importArticlesFromBibtex(file, principal);
        return ResponseEntity.ok("Imported successfully");
    }
}