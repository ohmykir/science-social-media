package org.example.sciencesocialmedia.controller;

import lombok.AllArgsConstructor;
import org.example.sciencesocialmedia.dto.CommentDTO;
import org.example.sciencesocialmedia.dto.LikeResponse;
import org.example.sciencesocialmedia.entity.User;
import org.example.sciencesocialmedia.service.CommentService;
import org.example.sciencesocialmedia.service.LikeService;
import org.example.sciencesocialmedia.service.SubscriptionService;
import org.example.sciencesocialmedia.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class InteractionController {

    private final SubscriptionService subscriptionService;
    private UserService userService;
    private LikeService likeService;
    private CommentService commentService;

    @PostMapping("/likes")
    public ResponseEntity<LikeResponse> toggleLike(@RequestBody Map<String, String> request, Principal principal) {
        String articleId = request.get("articleId");
        User user = userService.loadUserByUsername(principal.getName());

        boolean liked = likeService.toggleLike(articleId, user.getId());
        int likeCount = likeService.getLikeCount(articleId);

        return ResponseEntity.ok(new LikeResponse(liked, likeCount));
    }

    @PostMapping("/comments")
    public ResponseEntity<Void> addComment(@RequestBody CommentDTO commentDTO, Principal principal) {
        User user = userService.loadUserByUsername(principal.getName());
        commentService.addComment(commentDTO.getArticleId(), user.getUsername(), commentDTO.getText());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/subscribe/{userId}")
    public ResponseEntity<Void> toggleSubscription(@PathVariable String userId, Principal principal) {
        User user = userService.loadUserByUsername(principal.getName());
        subscriptionService.toggleSubscription(userId, user.getId());

        return ResponseEntity.ok().build();
    }
}

