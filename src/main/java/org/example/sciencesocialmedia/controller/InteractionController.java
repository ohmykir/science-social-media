package org.example.sciencesocialmedia.controller;

import lombok.AllArgsConstructor;
import org.example.sciencesocialmedia.dto.CommentDTO;
import org.example.sciencesocialmedia.entity.User;
import org.example.sciencesocialmedia.service.CommentService;
import org.example.sciencesocialmedia.service.LikeService;
import org.example.sciencesocialmedia.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class InteractionController {

    private UserService userService;
    private LikeService likeService;
    private CommentService commentService;

    @PostMapping("/likes")
    public ResponseEntity<Map<String, Object>> toggleLike(@RequestBody Map<String, String> request, Principal principal) {
        String articleId = request.get("articleId");
        User user = userService.loadUserByUsername(principal.getName());

        boolean liked = likeService.toggleLike(articleId, user.getId());
        int likeCount = likeService.getLikeCount(articleId);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("likeCount", likeCount);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/comments")
    public ResponseEntity<Void> addComment(@RequestBody CommentDTO commentDTO, Principal principal) {
        User user = userService.loadUserByUsername(principal.getName());
        commentService.addComment(commentDTO.getArticleId(), user.getUsername(), commentDTO.getText());
        return ResponseEntity.ok().build();
    }
}

