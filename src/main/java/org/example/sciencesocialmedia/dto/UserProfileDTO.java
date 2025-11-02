package org.example.sciencesocialmedia.dto;

import lombok.Data;
import org.example.sciencesocialmedia.entity.User;

import java.util.List;

@Data
public class UserProfileDTO {
    private User user;
    private int articleCount;
    private int subscriberCount;
    private int subscriptionCount;
    private boolean isSubscribed;
    private List<ArticleViewDTO> articles;
}
