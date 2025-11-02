package org.example.sciencesocialmedia.service;

import lombok.RequiredArgsConstructor;
import org.example.sciencesocialmedia.dto.ArticleViewDTO;
import org.example.sciencesocialmedia.dto.UserProfileDTO;
import org.example.sciencesocialmedia.dto.UserUpdateDTO;
import org.example.sciencesocialmedia.entity.Article;
import org.example.sciencesocialmedia.entity.User;
import org.example.sciencesocialmedia.exception.UserNotFoundException;
import org.example.sciencesocialmedia.repository.ArticleRepository;
import org.example.sciencesocialmedia.repository.SubscriptionRepository;
import org.example.sciencesocialmedia.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubscriptionRepository subscriptionRepository;
    private final ArticleRepository articleRepository;
    private final ArticleService articleService;

    @Override
    public User loadUserByUsername(String username) throws UserNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public UserProfileDTO getUserProfile(String userId, String currentUserId, String sortBy) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        int subscriberCount = subscriptionRepository.countSubscribersByAuthorId(userId);
        int subscriptionCount = subscriptionRepository.countSubscriptionsBySubscriberId(userId);

        List<Article> articles;
        switch (sortBy) {
            case "title" -> articles = articleRepository.findByAuthorIdOrderByTitleAsc(user.getId());
            case "year" -> articles = articleRepository.findByAuthorIdOrderByYearDesc(user.getId());
            case "likes" -> articles = articleService.getArticlesSortedByLikes(user.getId());
            case "comments" -> articles = articleService.getArticlesSortedByComments(user.getId());
            default -> articles = articleRepository.findByAuthorIdOrderByPublishedDesc(user.getId());
        }

        List<ArticleViewDTO> articleViewDTOs = articleService.mapToViewDTO(articles);

        boolean isSubscribed = currentUserId != null && !currentUserId.equals(userId) &&
                subscriptionRepository.existsByAuthorIdAndSubscriberId(userId, currentUserId);

        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setUser(user);
        userProfileDTO.setSubscriberCount(subscriberCount);
        userProfileDTO.setSubscriptionCount(subscriptionCount);
        userProfileDTO.setArticleCount(articles.size());
        userProfileDTO.setArticles(articleViewDTOs);
        userProfileDTO.setSubscribed(isSubscribed);

        return userProfileDTO;
    }

    public void updateProfile(String userId, UserUpdateDTO updateDTO, MultipartFile photo) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setFirstName(updateDTO.getFirstName());
        user.setLastName(updateDTO.getLastName());
        user.setEmail(updateDTO.getEmail());
        user.setBio(updateDTO.getBio());

        if (photo != null && !photo.isEmpty()) {
            String photoPath = saveProfilePhoto(photo, userId);
            user.setProfilePhotoPath(photoPath);
        }

        userRepository.save(user);
    }

    public boolean saveUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return false;
        }
        user.setRoles(user.getRoles());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        userRepository.save(user);

        return true;
    }

    public List<User> getSubscribers(String userId) {
        List<String> subscriberIds = subscriptionRepository.findSubscriberIdsByAuthorId(userId);
        return userRepository.findAllById(subscriberIds);
    }

    public List<User> getSubscriptions(String userId) {
        List<String> subscriptionIds = subscriptionRepository.findSubscriptionIdsBySubscriberId(userId);
        return userRepository.findAllById(subscriptionIds);
    }

    private String getUploadDir() {
        return System.getProperty("user.home") + File.separator + "mindlink-uploads" + File.separator + "profile-photos";
    }

    private String saveProfilePhoto(MultipartFile photo, String userId) throws IOException {
        String uploadDir = getUploadDir();
        File dir = new File(uploadDir);

        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created && !dir.exists()) {
                throw new IOException("Не удалось создать директорию");
            }
        }

        String fileName = userId + "_" + System.currentTimeMillis() + ".jpg";
        String filePath = uploadDir + File.separator + fileName;

        File file = new File(filePath);
        try {
            photo.transferTo(file);
        } catch (Exception e) {
            throw new IOException("Не удалось сохранить фото профиля: " + e.getMessage(), e);
        }

        return "/uploads/profile-photos/" + fileName;
    }
}
